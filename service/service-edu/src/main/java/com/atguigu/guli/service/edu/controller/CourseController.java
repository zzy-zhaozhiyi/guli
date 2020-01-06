package com.atguigu.guli.service.edu.controller;


import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.service.edu.entity.Course;
import com.atguigu.guli.service.edu.entity.form.CourseInfoForm;
import com.atguigu.guli.service.edu.entity.vo.CoursePublishVo;
import com.atguigu.guli.service.edu.entity.vo.CourseQueryVo;
import com.atguigu.guli.service.edu.service.CourseService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 课程 前端控制器
 * </p>
 *
 * @author Helen
 * @since 2019-12-25
 */
@Api("课程管理")
@CrossOrigin
@Slf4j
@RestController
@RequestMapping("admin/edu/course")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @PostMapping("save-course-info")
    public R saveCourseInfoForm(@RequestBody CourseInfoForm courseInfoForm) {
        //之所以返回id是因为前端要用
        String courseId = courseService.savaCourseInfoFrom(courseInfoForm);
        return R.ok().data("courseId", courseId);

    }

    @ApiOperation(value = "根据ID查询课程")
    @GetMapping("course-info/{id}")
    public R getById(@PathVariable String id) {
        CourseInfoForm courseInfoForm = courseService.getCourseInfoFormById(id);
        return R.ok().data("item", courseInfoForm);
    }

    @ApiOperation(value = "更新课程")
    @PutMapping("update-course-info")
    public R updateCourseInfoById(@RequestBody CourseInfoForm courseInfoForm) {
        courseService.updateCourseInfoById(courseInfoForm);
        return R.ok();
    }

    @ApiOperation(value = "分页课程列表")
    @GetMapping("{page}/{limit}")
    public R index(@PathVariable Long page, @PathVariable Long limit, CourseQueryVo courseQueryVo) {
        Page<Course> pageParam = new Page<>(page, limit);

        IPage<Course> courseIPage = courseService.selectPage(pageParam, courseQueryVo);

        List<Course> records = courseIPage.getRecords();

        long total = courseIPage.getTotal();

        return R.ok().data("total", total).data("rows", records);
    }

    @ApiOperation(value = "根据ID删除课程")
    @DeleteMapping("{id}")
    public R removeById(
            @ApiParam(name = "id", value = "课程ID", required = true)
            @PathVariable String id) {
        courseService.removeCourseById(id);
        return R.ok();
    }

    @ApiOperation(value = "根据ID获取课程发布信息")
    @GetMapping("course-publish-info/{id}")
    public R getCoursePublishVoById(@PathVariable String id) {
        CoursePublishVo courseInfoForm = courseService.getCoursePublishVoById(id);
        return R.ok().data("item", courseInfoForm);
    }
    @ApiOperation(value = "根据id发布课程")
    @PutMapping("publish-course/{id}")
    public R publishCourseById(@PathVariable String id){

        courseService.publishCourseById(id);
        return R.ok();
    }
}
