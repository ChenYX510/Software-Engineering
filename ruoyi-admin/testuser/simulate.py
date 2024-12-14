# -*- coding: utf-8 -*-

import os
import json
import argparse
import numpy as np
import geopandas as gpd
import math
import pandas as pd
import time

import sys
print("Python executable:", sys.executable)
print("Python version:", sys.version)

def get_grid_index_func(lat, lan, simulation_city):
    # 计算point的行列
    with open('city_O_point.json') as f:
        city_O_point = json.load(f)
    o_lat = city_O_point[simulation_city][0]
    o_lan = city_O_point[simulation_city][1]
    # 行
    row_index = (lat - o_lat) / 0.005
    # 列
    col_index = (lan - o_lan) / 0.005
    return math.floor(row_index), math.floor(col_index)

def simulation_task(R0, I_H_para, I_R_para, H_R_para, I_input, region_list, simulation_days, simulation_city, cur_dir_name):
    output_dir = f'./SimulationResult/unlock_result/{simulation_city}/{cur_dir_name}'
    os.makedirs(output_dir, exist_ok=True)

    # 保存初始参数到 JSON
    data = {
        "R0": R0,
        "I_H_para": I_H_para,
        "I_R_para": I_R_para,
        "H_R_para": H_R_para,
        "I_input": json.loads(I_input),
        "region_list": json.loads(region_list),
        "simulation_days": simulation_days,
        "simulation_city": simulation_city
    }
    with open(f'{output_dir}/data.json', 'w') as f:
        json.dump(data, f)

    # 加载网格索引
    with open(f"./{simulation_city}/grid_index_dic.json", 'r', encoding='utf-8') as f:
        grid_index_dict = json.load(f)

    # 加载城市网格 Shapefile
    shp_path = f'./{simulation_city}/city.shp'
    if not os.path.exists(shp_path):
        return {"msg": "no shp file"}
    grid_shp = gpd.GeoDataFrame.from_file(shp_path)
    grid_array = grid_shp['geometry'].to_numpy()

    # 初始化人口数据
    N_0 = np.load(f'./{simulation_city}/population.npy')
    S_0 = N_0.copy()
    I_0 = np.zeros_like(N_0)
    H_0 = np.zeros_like(N_0)
    R_0 = np.zeros_like(N_0)

    # 处理 I_input 和 region_list
    I_input_json = json.loads(I_input)
    I_input_items = I_input_json.items()
    I_input = []
    # 将 I_input 和 region_list 解析为 JSON 格式，并遍历它们，计算每个位置的网格索引，并更新对应的 S 和 I 数量
    for key, value in I_input_items:
        I_input.append(int(value))
    region_list_json = json.loads(region_list)
    for key, value in region_list_json.items():
        index = int(key)
        lat, lon = value
        index_x, index_y = get_grid_index_func(lat, lon, simulation_city)
        key_name = f'{lat}_{lon}'
        if key_name in grid_index_dict:
            xy_index_grid = grid_index_dict[key_name]
            S_0[xy_index_grid] = np.where(S_0[xy_index_grid] >= I_input[index], S_0[xy_index_grid] - I_input[index], 0)
            I_0[xy_index_grid] += I_input[index]

    # 开始模拟
    if simulation_city == 'yuzhong':
        OD = np.load(f'./OD_data/{simulation_city}/OD.npy')
        for time_index in range(simulation_days * 24):
            S_0, I_0, H_0, R_0 = simulate_step(
                time_index, S_0, I_0, H_0, R_0, N_0, OD[time_index, :, :], [R0, I_H_para, I_R_para, H_R_para], grid_array, output_dir
            )
    else:
        for file_index in range(simulation_days * 6):
            OD = np.load(f'./OD_data/{simulation_city}/OD_{file_index}.npz')
            OD = OD[OD.files[0]]
            for hour_index in range(4):
                S_0, I_0, H_0, R_0 = simulate_step(
                    file_index * 4 + hour_index, S_0, I_0, H_0, R_0, N_0, OD[:, :, hour_index],
                    [R0, I_H_para, I_R_para, H_R_para], grid_array, output_dir
                )

    return {"msg": "Simulation completed successfully!"}

def adjust_negative_values(array):
    """
    调整负值，将负值重新分配到正值。
    """
    while True in np.where(array < 0, True, False):
        negative_values = np.where(array < 0, array, 0)
        num_negative = np.where(array <= 0, 1, 0).sum()
        sum_negative = negative_values.sum() * -1
        array = np.where(array <= 0, 0, array - sum_negative / (len(array) - num_negative))
    return array


def simulate_step(time_index, S_0, I_0, H_0, R_0, N_0, OD, simulation_para, grid_array, output_dir):
    R0, I_H_para, I_R_para, H_R_para = simulation_para

    # 先算移动人中SIR的占比比率
    N_0 = np.around(N_0, 4)
    S_0 = np.around(S_0, 4)
    I_0 = np.around(I_0, 4)
    H_0 = np.around(H_0, 4)
    R_0 = np.around(R_0, 4)
    move_people = N_0 - H_0  # 具备移动能力的人

    grid_length = len(S_0)

    # 计算人口流动占比
    S_temp = np.divide(S_0, move_people, out=np.zeros_like(S_0), where=move_people > 0)
    I_temp = np.divide(I_0, move_people, out=np.zeros_like(I_0), where=move_people > 0)
    R_temp = np.divide(R_0, move_people, out=np.zeros_like(R_0), where=move_people > 0)

    # 计算流动人口分布
    S_people = S_temp * OD
    I_people = I_temp * OD
    R_people = R_temp * OD

    # 四舍五入
    S_people = np.around(S_people, 4)
    I_people = np.around(I_people, 4)
    R_people = np.around(R_people, 4)

    # 更新 S, I, R 的值
    S_0 = S_0 - S_people.sum(axis=1) + S_people.sum(axis=0)
    I_0 = I_0 - I_people.sum(axis=1) + I_people.sum(axis=0)
    R_0 = R_0 - R_people.sum(axis=1) + R_people.sum(axis=0)

    # 调整负值
    S_0 = adjust_negative_values(S_0)
    I_0 = adjust_negative_values(I_0)
    R_0 = adjust_negative_values(R_0)

    # 计算新增感染和恢复
    m_infected = np.divide(
        R0 * 0.01 * S_people.sum(axis=0) * I_people.sum(axis=0),
        OD.sum(axis=0),
        out=np.zeros_like(S_people.sum(axis=0)),
        where=(OD.sum(axis=0) > 0),
        )
    s_infected = np.divide(
        R0 * 0.01 * S_0 * I_0,
        N_0,
        out=np.zeros_like(S_0),
        where=(N_0 > 0),
        )
    new_infected = m_infected + s_infected

    new_hospital = I_0 * I_H_para
    new_I_recovered = I_0 * I_R_para
    new_H_recovered = H_0 * H_R_para

    # 更新 S, I, H, R 的值
    S_0 -= new_infected
    I_0 += new_infected - new_hospital - new_I_recovered
    H_0 += new_hospital - new_H_recovered
    R_0 += new_I_recovered + new_H_recovered

    # 再次调整负值
    S_0 = adjust_negative_values(S_0)
    I_0 = adjust_negative_values(I_0)
    H_0 = adjust_negative_values(H_0)
    R_0 = adjust_negative_values(R_0)

    # 更新总人口
    N_0 = S_0 + I_0 + H_0 + R_0

    # 保存结果
    np.save(
        f"{output_dir}/simulation_DSIHR_result_{time_index}.npy",
        np.vstack((grid_array, S_0, I_0, H_0, R_0, new_infected, N_0)),
    )
    # 保存csv格式，方便其他功能调用
    simulation_result = np.load(f"{output_dir}/simulation_DSIHR_result_{time_index}.npy", allow_pickle=True)
    simulation_result = simulation_result.T
    simulation_result = pd.DataFrame(simulation_result)
    simulation_result.columns = ['geometry', 'S', 'I', 'H', 'R', 'new_infected', 'total_num']
    simulation_result.to_csv(f"{output_dir}/simulation_DSIHR_result_{time_index}.csv")

    return S_0, I_0, H_0, R_0


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Run simulation task.")
    parser.add_argument("--R0", type=float, required=True, help="Basic reproduction number.")
    parser.add_argument("--I_H_para", type=float, required=True, help="Hospitalization parameter.")
    parser.add_argument("--I_R_para", type=float, required=True, help="Infected recovery parameter.")
    parser.add_argument("--H_R_para", type=float, required=True, help="Hospital recovery parameter.")
    parser.add_argument("--I_input", type=str, required=True, help="Initial infection data.")
    parser.add_argument("--region_list", type=str, required=True, help="Region list JSON.")
    parser.add_argument("--simulation_days", type=int, required=True, help="Number of simulation days.")
    parser.add_argument("--simulation_city", type=str, required=True, help="City name for simulation.")
    parser.add_argument("--cur_dir_name", type=str, required=True, help="cur_dir_name.")


    args = parser.parse_args()

    print(args.I_input)

    result = simulation_task(
        args.R0, args.I_H_para, args.I_R_para, args.H_R_para,
        args.I_input, args.region_list, args.simulation_days, args.simulation_city,args.cur_dir_name
    )
    print(result)
