package com.atguigu.guli.service.oss.service;

import java.io.InputStream;

/**
 * @author helen
 * @since 2019/12/30
 */
public interface FileService {

    /**
     * 实现阿里云文件上传
     * @param inputStream
     * @param module
     * @param originalFilename
     * @return
     */
    String upload(InputStream inputStream, String module, String originalFilename);

    void removeFile(String url);
}
