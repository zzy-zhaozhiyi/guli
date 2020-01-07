package com.atguigu.guli.service.vod.service;

import com.aliyuncs.exceptions.ClientException;

import java.io.InputStream;
import java.util.Map;

/**
 * @author helen
 * @since 2020/1/7
 */
public interface VideoService {

    /**
     *
     * @param inputStream,
     * @param originalFilename
     * @return 阿里云视频id
     */
    String uploadVideo(InputStream inputStream, String originalFilename);


    void removeVideo(String videoId) throws ClientException;

    Map<String, Object> getVideoUploadAuthAndAddress(String title, String fileName) throws ClientException;

    Map<String, Object> refreshVideoUploadAuth(String videoId) throws ClientException;

}
