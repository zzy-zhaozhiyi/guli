package com.atguigu.guli.service.statistics.service;

import com.atguigu.guli.service.statistics.entity.Daily;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 网站统计日数据 服务类
 * </p>
 *
 * @author zzy
 * @since 2020-01-09
 */
public interface DailyService extends IService<Daily> {

    void createStatisticsByDay(String day);

    Map<String, Object> showChart(String begin, String end, String type);
}
