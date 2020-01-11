package com.atguigu.guli.service.statistics.service.impl;

import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.service.statistics.client.UcenterClient;
import com.atguigu.guli.service.statistics.entity.Daily;
import com.atguigu.guli.service.statistics.mapper.DailyMapper;
import com.atguigu.guli.service.statistics.service.DailyService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 网站统计日数据 服务实现类
 * </p>
 *
 * @author zzy
 * @since 2020-01-09
 */
@Service
public class DailyServiceImpl extends ServiceImpl<DailyMapper, Daily> implements DailyService {
    @Autowired
    private UcenterClient ucenterClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createStatisticsByDay(String day) {
        //构建查询条件，看是否已存在，删除或者提示用户已经存在
        QueryWrapper<Daily> dateCalculated = new QueryWrapper<Daily>().eq("date_calculated", day);
        //查询这个是否存在
        int delete = this.baseMapper.delete(dateCalculated);


        R r = this.ucenterClient.registerCount(day);
        Integer registerCount = (Integer) r.getData().get("registerCount");

        Integer loginNum = RandomUtils.nextInt(100, 200); //TODO
        Integer videoViewNum = RandomUtils.nextInt(100, 200); //TODO
        Integer courseNum = RandomUtils.nextInt(100, 200); //TODO

        Daily daily = new Daily();
        daily.setRegisterNum(registerCount);
        daily.setLoginNum(loginNum);
        daily.setVideoViewNum(videoViewNum);
        daily.setCourseNum(courseNum);
        daily.setDateCalculated(day);

        this.baseMapper.insert(daily);


    }

    @Override
    public Map<String, Object> showChart(String begin, String end, String type) {
        //将x和y轴的信息分别求出，封装到map中
        HashMap<String, Object> map = new HashMap<>();
        //分装查询条件
        QueryWrapper<Daily> queryWrapper = new QueryWrapper<>();
        //选择查询的字段
        queryWrapper.select("date_calculated", type);
        queryWrapper.between("date_calculated", begin, end);
        //根据条件进行查询，里面的一个map就放了一行的数据，包括date_calculated和type
        List<Map<String, Object>> mapList = this.baseMapper.selectMaps(queryWrapper);
        //根据条件查询的结果，x、y是两个集合存储的
        ArrayList<String> xList = new ArrayList<>();
        ArrayList<Integer> yList = new ArrayList<>();


        for (Map<String, Object> mapData : mapList) {
            String calculated = (String) mapData.get("date_calculated");
            xList.add(calculated);
            Integer num = (Integer) mapData.get(type);
            yList.add(num);
        }

        //这里曾经的怀疑是map竟然只能放一个值，真是脑字啊
        map.put("xData",xList);
        map.put("yData",yList);

        return map;
    }
}
