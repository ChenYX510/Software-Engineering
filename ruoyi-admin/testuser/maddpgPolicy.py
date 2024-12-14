# -*- coding: utf-8 -*-
import os
import argparse
import numpy as np
import geopandas as gpd
import time
import json

# region -> grid
def grid_region_action(simulation_city, policy_cur_dir_name, cur_dir_name, grid_shp, region_action, day_index, policy_index):
    print('------------- grid_region_action start -------------')
    SIHR_file_path = f"./SimulationResult/unlock_result/{simulation_city}/{cur_dir_name}/simulation_DSIHR_result_{day_index}.npy"
    if os.path.exists(SIHR_file_path):
        SIHR_file = np.load(SIHR_file_path, allow_pickle=True)
    else:
        print('not find file')
        return False
    I_file = SIHR_file[2]
    del SIHR_file

    city_grid_num = {'guangzhou': 26923, 'wuxi': 15975, 'ezhou': 6293}
    grid_action = np.zeros(city_grid_num[simulation_city]) + 1
    # 划分成167份
    # load json
    with open("./"+simulation_city+"/grid_region_index.json", 'r', encoding='UTF-8') as f:
        grid_index_dict = json.load(f)
    for item_key in grid_index_dict.keys():
        grid_list = grid_index_dict[item_key]
        I_list = []
        for grid_index_item in grid_list:
            I_list.append(I_file[int(grid_index_item)])
        I_list = np.around(np.array(I_list), 4)

        # mean
        I_mean = np.mean(I_list)
        # std
        I_std = np.std(I_list)
        # print(I_std)
        normalization = np.where(I_std != 0, (I_list - I_mean) / I_std * 0.01, 0)
        # print(normalization)
        grid_action_item = np.around(region_action[policy_index, int(item_key)] + normalization, 4)
        for index, grid_index_item in enumerate(grid_list):
            # print(grid_index_item)
            grid_action[int(grid_index_item)] = grid_action_item[index]
    # np.save('grid_action' + str(policy_index) + '.npy', grid_action)
    print('------------- grid_region_action test1 -------------')
    grid_shp['quota'] = grid_action
    grid_shp.to_file('./grid_coefficient/'+simulation_city+'/' + policy_cur_dir_name + '/' + str(policy_index * 4) + '-' + str(policy_index * 4 + 3) + '_grid.shp',
                     driver='ESRI Shapefile', encoding='utf-8')
    return True


# 决策
def gridPolicyTask(simulation_city, grid_shp, action_result, cur_dir_name, policy_cur_dir_name):
    for policy_time in range(len(action_result)):  # 共73个决策时刻
        # print('policy_time', policy_time)
        func_return = grid_region_action(simulation_city, policy_cur_dir_name, cur_dir_name, grid_shp, action_result, policy_time * 4 + 3, policy_time)
        if not func_return:
            break


def MADDPGPolicy(simulation_city, policy_dir, simulation_days, cur_dir_name):
    action_file_path = f"./{simulation_city}/action.npy"
    # 检查动作文件是否存在
    if not os.path.exists(action_file_path):
        print(f"Action file not found: {action_file_path}")
        return
    else:
        action_data = np.load(action_file_path)

    os.makedirs(f"./grid_coefficient/{simulation_city}/{policy_dir}/")

    if simulation_city == 'chongqing':
        grid_shp = gpd.GeoDataFrame.from_file(f"./{simulation_city}/city.shp")
        grid_shp.insert(loc=1, column='quota', value=0)  # 在最后一列后，插入值全为3的c列
        for time_index_item in range(simulation_days * 6):
            action_item = action_data[time_index_item]
            grid_shp['quota'] = action_item
            grid_shp.to_file(f"./grid_coefficient/{simulation_city}/{policy_dir}/{time_index_item * 4}-{time_index_item * 4 + 3}_grid.shp",
                             driver='ESRI Shapefile', encoding='utf-8')
    else:
        # 根据OD文件得到action_node
        OD_data = np.load('./' + str(simulation_city) + '/OD.npy')
        # print(OD_data.shape)  # (744, 167, 167)
        action_result = []
        city_4_level_num = {'guangzhou': 167, 'ezhou': 26, 'wuxi': 92}
        action_shape_len = int(city_4_level_num[str(simulation_city)])
        for index, action_item in enumerate(action_data):
            OD_out_item = OD_data[index, :, :].sum(axis=-1)  # (167,)
            action_item = action_item.reshape(action_shape_len, action_shape_len)
            action_node_item = np.around(np.where(OD_out_item > 0, (action_item * OD_data[index, :, :]).sum(axis=-1) /
                                                  OD_out_item, 1), 2)
            action_result.append(action_node_item)
            if index > 180:
                break
        action_result = np.array(action_result)  # (73, 167)

        grid_shp = gpd.GeoDataFrame.from_file('./' + str(simulation_city) + '/city.shp')
        grid_shp.insert(loc=1, column='quota', value=0)  # 在最后一列后，插入值全为3的c列
        gridPolicyTask(simulation_city, grid_shp, action_result, cur_dir_name, policy_dir)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Process MADDPG policy.")
    # 模拟的城市名称
    parser.add_argument("--simulation_city", type=str, required=True, help="City name for simulation.")
    # 强化学习策略的文件路径（就是模拟开始的时间）
    parser.add_argument("--policy_dir", type=str, required=True, help="Directory to save the policy files.")
    # 模拟天数
    parser.add_argument("--simulation_days", type=int, required=True, help="Number of simulation days.")
    # 初始模拟的文件路径（时间）
    parser.add_argument("--cur_dir_name", type=str, required=True, help="cur_dir_name.")

    args = parser.parse_args()

    # 开始进行决策
    MADDPGPolicy(args.simulation_city, args.policy_dir, args.simulation_days, args.cur_dir_name)

