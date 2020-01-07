package com.atguigu.guli.service.vod.controller;

import com.aliyuncs.exceptions.ClientException;
import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.common.base.result.ResultCodeEnum;
import com.atguigu.guli.common.base.util.ExceptionUtils;
import com.atguigu.guli.service.base.exception.GuliException;
import com.atguigu.guli.service.vod.service.VideoService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Map;

/**
 * @author helen
 * @since 2020/1/7
 */
@RestController
@RequestMapping("/admin/vod/video")
@Api( "阿里云视频管理")
@CrossOrigin
@Slf4j
public class VideoController {

    @Autowired
    private VideoService videoService;

    @PostMapping("upload")
    private R uploadVideo(@RequestParam("file") MultipartFile file){
        try {
            InputStream inputStream = file.getInputStream();
            String originalFilename = file.getOriginalFilename();

            String videoId = videoService.uploadVideo(inputStream, originalFilename);

            return R.ok().message("视频上传成功").data("videoId", videoId);
        } catch (Exception e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new GuliException(ResultCodeEnum.VIDEO_UPLOAD_TOMCAT_ERROR);
        }

    }

    @DeleteMapping("remove/{vodId}")
    private R uploadVideo(@PathVariable("vodId") String vodId){
        try {
            videoService.removeVideo(vodId);

            //根据视频的vodId获取数据库 edu_video表的数据记录
            //清空`video_source_id``video_original_name`
            //更新video数据记录

            return R.ok().message("视频删除成功");
        } catch (Exception e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new GuliException(ResultCodeEnum.VIDEO_DELETE_ALIYUN_ERROR);
        }

    }

    //下面两个方法是通过js浏览器直传的
    @GetMapping("get-video-upload-auth-and-address/{title}/{fileName}")
    public R getVideoUploadAuthAndAddress(@PathVariable String title, @PathVariable String fileName){
        try {
            Map<String, Object> map = videoService.getVideoUploadAuthAndAddress(title, fileName);
            return R.ok().data(map).message("获取视频上传凭证和地址成功");
        } catch (ClientException e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new GuliException(ResultCodeEnum.FETCH_VIDEO_UPLOADAUTH_ERROR);
        }
    }


    @GetMapping("refresh-video-upload-auth/{videoId}")
    public R refreshVideoUploadAuth(@PathVariable String videoId){
        try {
            Map<String, Object> map = videoService.refreshVideoUploadAuth(videoId);
            return R.ok().data(map).message("刷新视频上传凭证成功");
        } catch (ClientException e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new GuliException(ResultCodeEnum.REFRESH_VIDEO_UPLOADAUTH_ERROR);
        }
    }
}
