package com.atguigu.guli.service.order.client;

import com.atguigu.guli.service.base.dto.CourseDto;
import com.atguigu.guli.service.order.client.exception.CourseFeignClientExceptionHandler;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author helen
 * @since 2020/1/14
 */
@FeignClient(value = "guli-edu", fallback = CourseFeignClientExceptionHandler.class)
public interface CourseFeignClient {

    @GetMapping(value = "/api/edu/course/inner/get-course-dto/{courseId}")
    public CourseDto getCourseDtoByCourseId(
            @PathVariable(value= "courseId") String courseId);


}
