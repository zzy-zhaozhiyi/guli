package com.atguigu.guli.service.edu.service;

import com.atguigu.guli.service.edu.entity.Teacher;
import com.atguigu.guli.service.edu.entity.vo.TeacherQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 讲师 服务类
 * </p>
 *
 * @author Helen
 * @since 2019-12-25
 */
public interface TeacherService extends IService<Teacher> {

    IPage<Teacher> selectPage(Page<Teacher> pageParam, TeacherQueryVo teacherQueryVo);

    List<Map<String, Object>> selectNameListByKey(String key);
}
