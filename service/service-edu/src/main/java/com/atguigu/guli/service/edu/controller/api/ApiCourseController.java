package com.atguigu.guli.service.edu.controller.api;

import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.service.edu.entity.Course;
import com.atguigu.guli.service.edu.entity.vo.ChapterVo;
import com.atguigu.guli.service.edu.entity.vo.WebCourseQueryVo;
import com.atguigu.guli.service.edu.entity.vo.WebCourseVo;
import com.atguigu.guli.service.edu.service.ChapterService;
import com.atguigu.guli.service.edu.service.CourseService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author zzy
 * @create 2020-01-08 19:10
 */
@CrossOrigin
@Api("课程")
@RestController
@RequestMapping("/api/edu/course")
public class ApiCourseController {

    @Autowired
    private CourseService courseService;


    @ApiOperation(value = "分页课程列表")
    @GetMapping(value = "page-list")
    public R pageList(@RequestParam Long page, @RequestParam Long limit, WebCourseQueryVo webCourseQueryVo){
        Page<Course> pageParam = new Page<Course>(page, limit);
        Map<String, Object> map = courseService.webSelectPage(pageParam, webCourseQueryVo);
        return  R.ok().data(map);
    }
    @Autowired
    private ChapterService chapterService;

    @ApiOperation(value = "根据ID查询课程")
    @GetMapping(value = "get/{courseId}")
    public R getByCourseId(@PathVariable String courseId){
        //查询课程信息和讲师信息
        WebCourseVo webCourseVo = courseService.selectWebCourseVoById(courseId);

        //查询当前课程的章节信息
        List<ChapterVo> chapterVoList = chapterService.nestedList(courseId);

        return R.ok().data("course", webCourseVo).data("chapterVoList", chapterVoList);
    }
}
