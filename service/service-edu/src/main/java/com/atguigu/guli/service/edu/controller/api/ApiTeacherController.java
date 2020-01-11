package com.atguigu.guli.service.edu.controller.api;

import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.service.edu.entity.Teacher;
import com.atguigu.guli.service.edu.service.TeacherService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author zzy
 * @create 2020-01-08 11:14
 */
@CrossOrigin
@Api("讲师")
@RestController
@RequestMapping("/api/edu/teacher")
public class ApiTeacherController {

    @Autowired
    private TeacherService teacherService;

    @ApiOperation(value = "所有讲师列表")
    @GetMapping("list")
    public R listAll() {
        List<Teacher> list = teacherService.list(null);
        return R.ok().data("items", list).message("获取讲师列表成功");
    }



    @ApiOperation(value = "分页讲师列表")
    @GetMapping(value = "page-list")
    public R pageList(@RequestParam Long page, @RequestParam Long limit) {
        Page<Teacher> pageParam = new Page<Teacher>(page, limit);
        Map<String, Object> map = teacherService.webSelectPage(pageParam);
        return R.ok().data(map);
    }
    @ApiOperation(value = "获取讲师")
    @GetMapping("get/{id}")
    public R get(@PathVariable String id) {
        Map<String, Object> map = teacherService.selectTeacherInfoById(id);
        return R.ok().data(map);
    }
}
