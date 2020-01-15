package com.atguigu.guli.service.base.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author zzy
 * @create 2020-01-14 11:44
 */
@Data
public class CourseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    //课程ID
    private String id;

    //课程标题
    private String title;

    //课程销售价格，设置为0则可免费观看
    private BigDecimal price;

    //课程封面图片路径
    private String cover;

    //课程讲师
    private String teacherName;
}
