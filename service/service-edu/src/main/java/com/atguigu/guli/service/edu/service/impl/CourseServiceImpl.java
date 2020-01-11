package com.atguigu.guli.service.edu.service.impl;

import com.atguigu.guli.service.edu.client.OssClient;
import com.atguigu.guli.service.edu.client.VodClient;
import com.atguigu.guli.service.edu.entity.*;
import com.atguigu.guli.service.edu.entity.form.CourseInfoForm;
import com.atguigu.guli.service.edu.entity.vo.CoursePublishVo;
import com.atguigu.guli.service.edu.entity.vo.CourseQueryVo;
import com.atguigu.guli.service.edu.entity.vo.WebCourseQueryVo;
import com.atguigu.guli.service.edu.entity.vo.WebCourseVo;
import com.atguigu.guli.service.edu.mapper.*;
import com.atguigu.guli.service.edu.service.CourseService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author Helen
 * @since 2019-12-25
 */
@Transactional
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {
    @Autowired
    private CourseDescriptionMapper courseDescriptionMapper;
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private ChapterMapper chapterMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private CourseCollectMapper courseCollectMapper;
    @Autowired
    private OssClient ossClient;
    @Autowired
    private VodClient vodClient;

    @Transactional
    @Override
    public String savaCourseInfoFrom(CourseInfoForm courseInfoForm) {

        //courseInfoForm 保护两张表的内容，所以要进行拆表
        //1.1、edu_course
        Course course = new Course();
        BeanUtils.copyProperties(courseInfoForm, course);
        course.setStatus(Course.COURSE_DRAFT);//草稿额状态
        int insert = this.baseMapper.insert(course);//这个insert是影响的行数，返回的不是生成的id
        //之所以要求id，因为下面保存edu_descripiton的id不是自动的，要用，他们是1:1的关系
        String id = course.getId();//保存后会生成id

        //1.2保存edu_course_description
        CourseDescription description = new CourseDescription();
        description.setDescription(courseInfoForm.getDescription());
        description.setId(id);
        this.courseDescriptionMapper.insert(description);
        return id;
    }

    @Override
    public CourseInfoForm getCourseInfoFormById(String id) {
        Course course = this.baseMapper.selectById(id);
        CourseDescription courseDescription = this.courseDescriptionMapper.selectById(id);
        CourseInfoForm courseInfoForm = new CourseInfoForm();
        BeanUtils.copyProperties(course, courseInfoForm);
        BeanUtils.copyProperties(courseDescription, courseInfoForm);

        return courseInfoForm;
    }

    @Override
    public void updateCourseInfoById(CourseInfoForm courseInfoForm) {
        //保存课程基本信息
        Course course = new Course();
        BeanUtils.copyProperties(courseInfoForm, course);
        baseMapper.updateById(course);

        //保存课程详情信息
        CourseDescription courseDescription = new CourseDescription();
        courseDescription.setDescription(courseInfoForm.getDescription());
        courseDescription.setId(course.getId());
        courseDescriptionMapper.updateById(courseDescription);
    }

    @Override
    public IPage<Course> selectPage(Page<Course> pageParam, CourseQueryVo courseQueryVo) {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("gmt_create");
        if (courseQueryVo == null) {
            IPage<Course> courseIPage = baseMapper.selectPage(pageParam, queryWrapper);

            return courseIPage;
        }

        String title = courseQueryVo.getTitle();
        String teacherId = courseQueryVo.getTeacherId();
        String subjectParentId = courseQueryVo.getSubjectParentId();
        String subjectId = courseQueryVo.getSubjectId();

        if (!StringUtils.isEmpty(title)) {
            queryWrapper.like("title", title);
        }

        if (!StringUtils.isEmpty(teacherId)) {
            queryWrapper.eq("teacher_id", teacherId);
        }

        if (!StringUtils.isEmpty(subjectParentId)) {
            queryWrapper.eq("subject_parent_id", subjectParentId);
        }

        if (!StringUtils.isEmpty(subjectId)) {
            queryWrapper.eq("subject_id", subjectId);
        }

        IPage<Course> courseIPage = baseMapper.selectPage(pageParam, queryWrapper);

        return courseIPage;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeCourseById(String id) {

        //删除关联数据

        //在此处调用oss中的删除图片文件的接口
        Course course = this.baseMapper.selectById(id);
        String cover = course.getCover();
        this.ossClient.removeFile(cover);


        //在此处调用vod中的删除视频文件的接口
        QueryWrapper<Video> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("video_source_id");
        queryWrapper.eq("course_id", id);
        List<Map<String, Object>> maps = videoMapper.selectMaps(queryWrapper);
        List<String> videoSourceIdList = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            String videoSourceId = (String)map.get("video_source_id");
            videoSourceIdList.add(videoSourceId);
        }
        vodClient.removeVideoByIdList(videoSourceIdList);
        //收藏信息：course_collect
        QueryWrapper<CourseCollect> courseCollectQueryWrapper = new QueryWrapper<>();
        courseCollectQueryWrapper.eq("course_id", id);
        courseCollectMapper.delete(courseCollectQueryWrapper);

        //评论信息：comment
        QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
        commentQueryWrapper.eq("course_id", id);
        commentMapper.delete(commentQueryWrapper);

        //课时信息：video
        QueryWrapper<Video> videoQueryWrapper = new QueryWrapper<>();
        videoQueryWrapper.eq("course_id", id);
        videoMapper.delete(videoQueryWrapper);

        //章节信息：chapter
        QueryWrapper<Chapter> chapterQueryWrapper = new QueryWrapper<>();
        chapterQueryWrapper.eq("course_id", id);
        chapterMapper.delete(chapterQueryWrapper);

        //课程详情：course_description
        courseDescriptionMapper.deleteById(id);

        //课程：course
        baseMapper.deleteById(id);
    }

    @Override
    public CoursePublishVo getCoursePublishVoById(String id) {
        return baseMapper.selectCoursePublishVoById(id);
    }

    @Override
    public void publishCourseById(String id) {
        Course course = new Course();
        course.setId(id);
        course.setStatus(Course.COURSE_NORMAL);
        baseMapper.updateById(course);
    }

    @Override
    public Map<String, Object> webSelectPage(Page<Course> pageParam, WebCourseQueryVo webCourseQueryVo) {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(webCourseQueryVo.getSubjectParentId())) {
            queryWrapper.eq("subject_parent_id", webCourseQueryVo.getSubjectParentId());
        }

        if (!StringUtils.isEmpty(webCourseQueryVo.getSubjectId())) {
            queryWrapper.eq("subject_id", webCourseQueryVo.getSubjectId());
        }

        if (!StringUtils.isEmpty(webCourseQueryVo.getBuyCountSort())) {
            queryWrapper.orderByDesc("buy_count");
        }

        if (!StringUtils.isEmpty(webCourseQueryVo.getGmtCreateSort())) {
            queryWrapper.orderByDesc("gmt_create");
        }

        if (!StringUtils.isEmpty(webCourseQueryVo.getPriceSort())) {
            queryWrapper.orderByDesc("price");
        }

        baseMapper.selectPage(pageParam, queryWrapper);

        List<Course> records = pageParam.getRecords();
        long current = pageParam.getCurrent();
        long pages = pageParam.getPages();
        long size = pageParam.getSize();
        long total = pageParam.getTotal();
        boolean hasNext = pageParam.hasNext();
        boolean hasPrevious = pageParam.hasPrevious();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("items", records);
        map.put("current", current);
        map.put("pages", pages);
        map.put("size", size);
        map.put("total", total);
        map.put("hasNext", hasNext);
        map.put("hasPrevious", hasPrevious);

        return map;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public WebCourseVo selectWebCourseVoById(String courseId) {
        //更新课程浏览数
        Course course = baseMapper.selectById(courseId);
        course.setViewCount(course.getViewCount() + 1);
        baseMapper.updateById(course);
        //获取课程信息
        return baseMapper.selectWebCourseVoById(courseId);
    }
}



