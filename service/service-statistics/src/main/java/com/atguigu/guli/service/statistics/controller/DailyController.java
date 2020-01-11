package com.atguigu.guli.service.statistics.controller;


import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.service.statistics.service.DailyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 网站统计日数据 前端控制器
 * </p>
 *
 * @author zzy
 * @since 2020-01-09
 */
@Api("统计分析管理")
@CrossOrigin
@RestController
@RequestMapping("/admin/statistics/daily")
public class DailyController {

    @Autowired
    private DailyService dailyService;

    @ApiOperation(value = "生成统计记录", notes = "生成统计记录")
    @PostMapping("create/{day}")
    public R createStatisticsByDate(@PathVariable String day) {
        dailyService.createStatisticsByDay(day);
        return R.ok().message("生成数据成功");
    }
    @ApiOperation("查询符合条件的数据")
@GetMapping("show-chart/{begin}/{end}/{type}")
    public  R showChart(@PathVariable("begin") String begin,@PathVariable("end")String end,@PathVariable("type")String type){
        Map <String,Object> map= this.dailyService.showChart(begin,end,type);
        return R.ok().data(map);
    }
}

