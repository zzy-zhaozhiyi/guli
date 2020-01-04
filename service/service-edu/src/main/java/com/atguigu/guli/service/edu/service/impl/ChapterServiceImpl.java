package com.atguigu.guli.service.edu.service.impl;

import com.atguigu.guli.service.edu.entity.Chapter;
import com.atguigu.guli.service.edu.entity.Video;
import com.atguigu.guli.service.edu.entity.vo.ChapterVo;
import com.atguigu.guli.service.edu.entity.vo.VideoVo;
import com.atguigu.guli.service.edu.mapper.ChapterMapper;
import com.atguigu.guli.service.edu.mapper.VideoMapper;
import com.atguigu.guli.service.edu.service.ChapterService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author Helen
 * @since 2019-12-25
 */
@Service
public class ChapterServiceImpl extends ServiceImpl<ChapterMapper, Chapter> implements ChapterService {
    @Autowired
    private VideoMapper videoMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeChapterById(String id) {

        //根据章节id删除所有阿里云视频数据
        //TODO

        //根据章节id删除所有视频
        QueryWrapper<Video> queryWrapperVideo = new QueryWrapper<>();
        queryWrapperVideo.eq("chapter_id", id);
        videoMapper.delete(queryWrapperVideo);

        //根据章节id删除章节
        baseMapper.deleteById(id);
    }

    @Override
    public List<ChapterVo> nestedList(String courseId) {
        //这是一个2级连查，和电商的3级联查很像，两种方式，一种：下面这种，两种：配置xml


        //根据courseid来查询所有的章节
        QueryWrapper<Chapter> chapterQueryWrapper = new QueryWrapper<Chapter>().eq("course_id", courseId).orderByAsc("sort", "id");
        List<Chapter> chapterList = this.baseMapper.selectList(chapterQueryWrapper);
        //根据courseid来查询属于这个课程的所有视频
        QueryWrapper<Video> videoQueryWrapper = new QueryWrapper<Video>().eq("course_id", courseId).orderByAsc("sort", "id");
        List<Video> videoList = this.videoMapper.selectList(videoQueryWrapper);

        //创建chaptervo集合进行填充
        ArrayList<ChapterVo> chapterVos = new ArrayList<>();
        //遍历章节和视频集合，填充一个chaptervo，再往集合添加
        for (Chapter chapter : chapterList) {
            ChapterVo chapterVo = new ChapterVo();
            BeanUtils.copyProperties(chapter, chapterVo);

            List<VideoVo> videoVos = new ArrayList<>();
            for (Video video : videoList) {
                if(chapter.getId().equals(video.getChapterId())) {
                    VideoVo videoVo = new VideoVo();
                    BeanUtils.copyProperties(video, videoVo);
                    videoVos.add(videoVo);
                }
            }
            chapterVo.setChildren(videoVos);
            //填充完一个以后，加入到chaptervos中
            chapterVos.add(chapterVo);
        }
        return chapterVos;
    }
}
