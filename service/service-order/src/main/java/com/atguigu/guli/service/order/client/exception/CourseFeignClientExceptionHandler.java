package com.atguigu.guli.service.order.client.exception;

import com.atguigu.guli.service.base.dto.CourseDto;
import com.atguigu.guli.service.order.client.CourseFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author helen
 * @since 2020/1/14
 */
@Component
@Slf4j
public class CourseFeignClientExceptionHandler implements CourseFeignClient {

    @Override
    public CourseDto getCourseDtoByCourseId(String courseId) {
        log.error("熔断器被执行");
        return new CourseDto();
    }
}
