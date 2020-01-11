package com.atguigu.guli.service.edu.controller;


import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.service.edu.entity.form.VideoInfoForm;
import com.atguigu.guli.service.edu.service.VideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zzy
 * @since 2019-12-25
 */
@Api("课时管理")
@CrossOrigin //跨域
@RestController
@RequestMapping("/admin/edu/video")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @ApiOperation(value = "新增课时")
    @PostMapping("save")
    public R save(@RequestBody VideoInfoForm videoInfoForm){
        videoService.saveVideoInfo(videoInfoForm);
        return R.ok().message("课时保存成功");
    }

    @ApiOperation(value = "根据ID查询课时")
    @GetMapping("get/{id}")
    public R getVideInfoById(@PathVariable String id){

        VideoInfoForm videoInfoForm = videoService.getVideoInfoFormById(id);
        return R.ok().data("item", videoInfoForm);
    }

    @ApiOperation(value = "更新课时")
    @PutMapping("update")
    public R updateCourseInfoById(@RequestBody VideoInfoForm videoInfoForm){

        videoService.updateVideoInfoById(videoInfoForm);
        return R.ok().message("课时修改成功");
    }

    @ApiOperation(value = "根据ID删除课时")
    @DeleteMapping("remove/{id}")
    public R removeById(@PathVariable String id){

        videoService.removeVideoById(id);
        return R.ok();
    }

}

