package com.atguigu.guli.service.edu.service.impl;

import com.atguigu.guli.service.edu.entity.Course;
import com.atguigu.guli.service.edu.entity.Teacher;
import com.atguigu.guli.service.edu.entity.vo.TeacherQueryVo;
import com.atguigu.guli.service.edu.mapper.CourseMapper;
import com.atguigu.guli.service.edu.mapper.TeacherMapper;
import com.atguigu.guli.service.edu.service.TeacherService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 讲师 服务实现类
 * </p>
 *
 * @author Helen
 * @since 2019-12-25
 */
@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {

    @Autowired
    private CourseMapper courseMapper;

    @Override
    public IPage<Teacher> selectPage(Page<Teacher> pageParam, TeacherQueryVo teacherQueryVo) {

        QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort");

        if (teacherQueryVo == null) {
            return baseMapper.selectPage(pageParam, queryWrapper);
        }

        String name = teacherQueryVo.getName();
        Integer level = teacherQueryVo.getLevel();
        String joinDateBegin = teacherQueryVo.getJoinDateBegin();
        String joinDateEnd = teacherQueryVo.getJoinDateEnd();

        if (!StringUtils.isEmpty(name)) {
            queryWrapper.eq("name", name);//TODO 优化
        }

        if (level != null) {
            queryWrapper.eq("level", level);
        }

        if (!StringUtils.isEmpty(joinDateBegin)) {
            queryWrapper.ge("join_date", joinDateBegin);
        }

        if (!StringUtils.isEmpty(joinDateEnd)) {
            queryWrapper.le("join_date", joinDateEnd);
        }

        return baseMapper.selectPage(pageParam, queryWrapper);
    }

    @Override
    public List<Map<String, Object>> selectNameListByKey(String key) {
        QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("name");
        queryWrapper.likeRight("name", key);
        //select name from edu_teacher where name like 'key%'
        List<Map<String, Object>> nameList = baseMapper.selectMaps(queryWrapper);
        return nameList;
    }

    @Override
    public Map<String, Object> webSelectPage(Page<Teacher> pageParam) {
        QueryWrapper<Teacher> queryWrapper = new QueryWrapper<Teacher>().orderByAsc("sort");
        //这里通过这部的查询的话，查询的结果就会自动的回添到pageParam里面，和查询后返回值，在get是一样的
        this.baseMapper.selectPage(pageParam, queryWrapper);
        long total = pageParam.getTotal();
        List<Teacher> records = pageParam.getRecords();
        long size = pageParam.getSize();
        long current = pageParam.getCurrent();
        long pages = pageParam.getPages();
        boolean hasNext = pageParam.hasNext();
        boolean hasPrevious = pageParam.hasPrevious();

        HashMap<String, Object> map = new HashMap<>();
        map.put("total", total);
        map.put("records", records);
        map.put("size", size);
        map.put("current", current);
        map.put("pages", pages);
        map.put("hasNext", hasNext);
        map.put("hasPrevious", hasPrevious);


        return map;
    }

    @Override
    public Map<String, Object> selectTeacherInfoById(String id) {
        //查询老师的信息
        Teacher teacher = this.baseMapper.selectById(id);
         //根据老师的id来查询课程信息
        List<Course> courses = this.courseMapper.selectList(new QueryWrapper<Course>().eq("teacher_id", id));
        HashMap<String, Object> map = new HashMap<>();
        map.put("teacher", teacher);
        map.put("courses", courses);
        return map;
    }


//    /**
//     * <p>
//     * 删除不存在的逻辑上属于成功
//     * </p>
//     *
//     * @param result 数据库操作返回影响条数
//     * @return boolean
//     */
//    private static boolean delBool(Integer result) {
//        return null != result && result > 0;
//    }
//
//    @Transactional(rollbackFor = Exception.class)
//    @Override
//    public boolean removeById(Serializable id) {
//        return delBool(baseMapper.deleteById(id));
//    }
}
