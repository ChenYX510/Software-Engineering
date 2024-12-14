import sys
import os
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '3'  # 只记录 ERROR 级别的消息
import pandas as pd
import numpy as np
import math
import matplotlib.pyplot as plt
from datetime import datetime
import matplotlib.pyplot
import numpy as np
import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt
import matplotlib as mpl
import json
import scipy.stats as stats
from scipy.stats import gamma
import warnings
import scipy.integrate as spi
import math

from flask import Flask, jsonify, render_template, request  # 导入Flask模块
from flask import send_from_directory, abort

import random
import os
import pickle
import matplotlib.pyplot as plt
import matplotlib
import time
import sys
import json
import numpy as np
import timeit
import tensorflow as tf

import sys

# from osgeo import ogr
from matplotlib.patches import Polygon  # 上色
import re
import imageio
import pandas as pd
# 设置colormap的数据
import matplotlib as mpl
import timeit
import geopandas as gpd
import geopandas
from shapely.geometry import Point, Polygon, shape
import json
import logging
# 使用flask_sqlalchemy进行数据库连接
from sqlalchemy import func
from datetime import datetime
import networkx
import math
import numpy
from itertools import compress
from concurrent.futures import ThreadPoolExecutor
from datetime import datetime, date


class RtModel:
    # This is our estimated Rt object when using the parametric function
    class ParametricOutput:
        def __init__(self, a_posterior, b_posterior, mean_posterior, std_posterior, t_start, t_end, real_dates):
            self.a_posterior = a_posterior
            self.b_posterior = b_posterior
            self.mean_posterior = mean_posterior
            self.median_posterior = stats.gamma.ppf(0.5, a_posterior, scale=b_posterior)
            self.std_posterior = std_posterior
            self.t_start = t_start
            self.t_end = t_end
            self.real_dates = real_dates[len(real_dates) - len(mean_posterior):]
            self.dataframe = self._to_dataframe()

        def _to_dataframe(self):
            """
            Converts the parametric Rt estimates into a pandas dataframe that can be used for plotting or other.
            The dataframe also includes key summary statistics such as mean, median, standard deviation and confidence intervals

            Returns:
            ----------
            Pandas dataframe with all Rt statistics and dates
            """
            low_ci_05 = stats.gamma.ppf(0.000005, self.a_posterior, scale=self.b_posterior)
            # low_ci_05 = low_ci_05*0.8
            high_ci_95 = stats.gamma.ppf(0.999995, self.a_posterior, scale=self.b_posterior)
            # high_ci_95 = high_ci_95*1.2
            low_ci_025 = stats.gamma.ppf(0.025, self.a_posterior, scale=self.b_posterior)
            high_ci_975 = stats.gamma.ppf(0.975, self.a_posterior, scale=self.b_posterior)

            rt_parametric_df = pd.DataFrame(data={'dates': self.real_dates,
                                                  'Rt_mean': self.mean_posterior,
                                                  'Rt_median': self.median_posterior,
                                                  'low_ci_05': low_ci_05,
                                                  'high_ci_95': high_ci_95,
                                                  'low_ci_025': low_ci_025,
                                                  'high_ci_975': high_ci_975})
            rt_parametric_df['dates'] = pd.to_datetime(rt_parametric_df['dates'])
            # Rt = round(estimated_rt_parametric.dataframe['Rt_mean'].tail(1).mean(), 3)
            return rt_parametric_df



    def _discr_si(self, k, mu, sigma):
        assert sigma > 0, "Error: sigma must be >0"
        assert mu > 1, "Error: mu must be <=1"
        assert all(k) >= 0, "Error: values in k must all be >0"

        # Shift -1
        a = ((mu - 1) / sigma) ** 2  # shape
        b = sigma ** 2 / (mu - 1)  # scale

        res = k * gamma.cdf(k, a, scale=b) + (k - 2) * gamma.cdf(k - 2,
                                                                 a, scale=b) - 2 * (k - 1) * gamma.cdf(k - 1, a,
                                                                                                       scale=b)

        res = res + a * b * (2 * gamma.cdf(k - 1, a + 1, scale=b) -
                             gamma.cdf(k - 2, a + 1, scale=b) - gamma.cdf(k, a + 1, scale=b))

        # Return largest of 0 or calculated value
        res2 = [max(0, x) for x in res]

        return np.array(res2)

    def _calc_time_windows(self, incidence, win_start=1, win_end=7):

        total_time = len(incidence)
        t_start = np.arange(win_start, total_time - (win_end - win_start))
        t_end = np.arange(win_end, total_time)

        nb_time_periods = len(t_start)

        return t_start, t_end, nb_time_periods

    def _posterior_from_si_distrib(self, incidence, si_distr, a_prior, b_prior, t_start, t_end):

        distrib_range = np.arange(0, len(si_distr))
        final_mean_si = np.sum(si_distr * distrib_range)
        lam = self._overall_infectivity(incidence, si_distr)

        posteriors = np.empty((len(t_start), 2))

        for i, start in enumerate(t_start):
            if t_end[i] > final_mean_si:
                a_post = a_prior + (incidence[start: t_end[i] + 1]).sum()['Cases']
                b_post = 1 / (1 / b_prior + np.sum(lam[start:t_end[i] + 1]))
                posteriors[i] = a_post, b_post
            else:
                posteriors[i] = np.nan, np.nan

        return posteriors 
    

    def _overall_infectivity(self, incidence, si_distr):
        incidence = incidence['Cases']
        # Calculate infectivity. To do that we calculate all the infections to that day which we multiply be the
        # probability of
        # infection (the serial distribution flipped)
        T = len(incidence)
        lam_t_vector = np.empty(T)
        lam_t_vector[0] = np.nan
        lam_t_vector[1:] = [np.sum(np.flip(incidence[0:i + 1]) * si_distr[0:i + 1]) for i in range(1, T)]
        return lam_t_vector


    def calc_parametric_rt(self, incidence, mean_si, std_si, win_start=1, win_end=7, mean_prior=5, std_prior=5):
        # Find how many time periods we have
        T = len(incidence)

        real_dates = incidence.index

        # Create our discretized serial interval distribution
        si_distribution = self._discr_si(np.arange(0, T), mean_si, std_si)

        # Fill our overflowing distribution with 0s (no prob)
        if len(si_distribution) < (T + 1):
            over = -(len(si_distribution) - (T + 1))
            si_distribution = np.pad(si_distribution, (0, over), 'constant')

        # Return out time windows and number of time periods based on the starting and ending time periods
        t_start, t_end, nb_time_periods = self._calc_time_windows(
            incidence, win_start=win_start, win_end=win_end)

        # Calculate the parameters of our gamma prior based on the provided mean and std of the serial interval
        a_prior = (mean_prior / std_prior) ** 2
        b_prior = std_prior ** 2 / mean_prior

        # Calculate our posteriors from our serial interval distribution
        post = self._posterior_from_si_distrib(
            incidence, si_distribution, a_prior, b_prior, t_start, t_end)
        a_posterior, b_posterior = post[:, 0], post[:, 1]

        mean_posterior = a_posterior * b_posterior
        std_posterior = np.sqrt(a_posterior) * b_posterior

        result = self.ParametricOutput(a_posterior, b_posterior, mean_posterior,
                                       std_posterior, t_start, t_end, real_dates)
        return result



def get_Rt(incidence):
    model = RtModel()
    estimated_rt_parametric = model.calc_parametric_rt(incidence, mean_si=3.25, std_si=0.84, win_start=1, win_end=7,
                                                       mean_prior=2, std_prior=2)
   
    Rt_mean = round(estimated_rt_parametric.dataframe['Rt_mean'], 3)
    Rt_median = round(estimated_rt_parametric.dataframe['Rt_median'], 3)
    Rt_low_05 = round(estimated_rt_parametric.dataframe['low_ci_05'], 3)
    Rt_high_95 = round(estimated_rt_parametric.dataframe['high_ci_95'], 3)
    i=1
    for item in Rt_mean:
        i += 1
    return Rt_mean, Rt_low_05, Rt_high_95

def SIHR(inivalue, time_step, beta, N, I_H_para, I_R_para, H_R_para, newI, max_index):
    X = inivalue
    Y = np.zeros(4)
    # SIHR
    # S数量
    if time_step < len(beta):
        curBeta = beta[time_step]
    else:
        k = np.array(newI)
        incidence = {"Cases": k}
        incidence = pd.core.frame.DataFrame(incidence)
        data = get_Rt(incidence)
        data = data[0].tolist()[-1]
        curBeta = np.round(data/5, 4)
        curBeta = curBeta + 0.0007 * math.exp((time_step - max_index + 6) * 0.115)
    c = 1e2
    noise = curBeta*math.exp(-c*(time_step+1))
    curBeta = curBeta + noise
    Y[0] = round(X[0] - (curBeta * X[0] * X[1]) / N, 4)

    # I数量
    Y[1] = round(X[1] + (curBeta * X[0] * X[1]) / N - I_H_para * X[1] - I_R_para * X[1], 4)
    newI.append(Y[1])
    # H数量
    Y[2] = round(X[2] + I_H_para * X[1] - H_R_para * X[2], 4)
    # R数量
    Y[3] = round(X[3] + I_R_para * X[1] + H_R_para * X[2], 4)
    return Y

def Rt_process(Rt):
    Rt = Rt.tolist()
    max_value = max(Rt)
    max_idx = Rt.index(max_value)
    return max_idx


def plotPic(table_data, data):
    length = len(table_data)
    x = []
    for i in range(length):
        x.append(table_data['date'][i].strftime("%Y-%m-%d, %H:%M:%S")[:10])
    x = x[7:]
    startDate = str(table_data['date'][7])[:10]
    endDate = str(table_data['date'][length-1])[:10]
    sns.set(font_scale=1.5)
    # 修改默认设置
    mpl.rcParams["font.family"] = 'Times New Roman'  # 默认字体类型
    mpl.rcParams["mathtext.fontset"] = 'cm'  # 数学文字字体
    mpl.rcParams["font.size"] = 20  # 字体大小
    mpl.rcParams["axes.linewidth"] = 1  # 轴线边框粗细（默认的太粗了）
    font = {'family': 'Times New Roman',
            'color': 'black',
            'weight': 'normal',
            'size': 25,
            }

    region = '1'

    fig, ax = plt.subplots()
    mean = data[0].tolist()
    y = data[1].tolist()
    y1 = data[2].tolist()
    zhfont1 = matplotlib.font_manager.FontProperties(fname="SourceHanSansSC-Normal.otf")

    plt.fill_between(x, y, y1, color='blue', alpha=0.15, label='95% CI')
    plt.ylim(0, None)
    from matplotlib.ticker import MultipleLocator, FormatStrFormatter
    ymajorLocator = MultipleLocator(1)
    ax.yaxis.set_major_locator(ymajorLocator)
    ax.yaxis.grid(True, which='major')  # x坐标轴的网格使用主刻度
    plt.plot(x, mean, 'k', linewidth=0.5, label='Rt')
    plt.plot([startDate, endDate], [1, 1], color="green", linestyle='--')
    plt.xlabel('Date')
    plt.ylabel('Rt')
    plt.title("Rt", fontproperties=zhfont1, fontsize=15)
    plt.xticks(range(1, len(x), 6))

    plt.rcParams.update({'font.size': 5})
    plt.legend(frameon=False, shadow=False, loc="upper right", ncol=1, prop=zhfont1)
    plt.rcParams.update({'font.size': 5})
    #plt.show()


def R0(table_data):
    # 首先模拟一列k，即每日的确诊病例数
    newInfectionList = table_data['Cases'].values.tolist()

    k = np.array(newInfectionList)
    if len(k) <= 1:
        return
    incidence = {"Cases": k}
    incidence = pd.DataFrame(incidence)
    data = get_Rt(incidence)
    plotPic(table_data, data)
    result = data[0].tolist()
    return result


def D_SIHRModel(I_H_para, I_R_para, H_R_para, filepath):
    table_data = pd.read_excel(filepath)
    # table_data = pd.read_excel('newGZ_Daily_Infected.xlsx')
    N = 18676600          # 湖北省为6000 0000, 广州市18676600     8681(10月28日)
    I_0 = 391
    H_0 = 303
    R_0 = 8290
    S_0 = N - I_0 - H_0 - R_0


    Rt = np.array(R0(table_data))  # 重庆11/06

    D = 4.5  # 4.5, 4.3/4/5/6/7/8
    # 参数
    beta = np.round(Rt/D, 4)
    # I_H_para = 0.0136  # 百分比
    # I_R_para = 0.1922  # 百分比
    # H_R_para = 0.111  # 百分比

    INI = [S_0, I_0, H_0, R_0]

    newI = []

    # T_range = np.arange(0, T+1)
    item = INI
    Res = []
    Res.append(item)
    cha = []
    max_index = Rt_process(Rt)
    for i in range(len(table_data)):
        if i > max_index:
            I_H_para = 0.0196
            I_R_para = 0.208
            # H_R_para = 0.111
            beta = beta + 0.0007 * math.exp((i-max_index+6) * 0.065)
        temp = item
        item = SIHR(item, i, beta, N, I_H_para, I_R_para, H_R_para, newI, max_index)
        cha.append(item[1] - temp[1])
        Res.append(item)

    Res = np.array(Res)
    # Res = spi.odeint(SIHR, INI, T_range)
    S_t = Res[:, 0]
    I_t = Res[:, 1]
    H_t = Res[:, 2]
    R_t = Res[:, 3]

    length = len(table_data)
    x = []
    for i in range(length):
        x.append(table_data['date'][i].strftime("%Y-%m-%d, %H:%M:%S")[:10])
    x = x[7:]

    history = table_data['Cases'].values.tolist()[7:]
    result = {
        "DSIHRData": cha,
        "HistoryData": history,
        "Date": x
    }
    return result



if __name__ == '__main__':
    if len(sys.argv) != 5:
        print("Usage: python script.py <I_H_para> <I_R_para> <H_R_para> <filepath>")
        sys.exit(1)

    I_H_para = float(sys.argv[1])
    I_R_para = float(sys.argv[2])
    H_R_para = float(sys.argv[3])
    filepath = sys.argv[4]

    result = D_SIHRModel(I_H_para, I_R_para, H_R_para, filepath)

    import json
    print(json.dumps(result, ensure_ascii=False, indent=4))
#"D:\广播台\韧性城市智能规划\韧性城市智能规划\项目输入数据归档\广州\GZ_Daily_Infected.xlsx"   0.0136 0.1922 0.111
#python getDSIHR.py 0.0136 0.1922 0.111 D:\广播台\韧性城市智能规划\韧性城市智能规划\项目输入数据归档\广州\GZ_Daily_Infected.xlsx