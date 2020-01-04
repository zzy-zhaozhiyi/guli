package com.atguigu.guli.service.oss.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CannedAccessControlList;
import com.atguigu.guli.service.oss.service.FileService;
import com.atguigu.guli.service.oss.util.OssProperties;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.UUID;

/**
 * @author helen
 * @since 2019/12/30
 */
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private OssProperties ossProperties;

    @Override
    public String upload(InputStream inputStream, String module, String originalFilename) {

        String endpoint = ossProperties.getEndpoint();
        String keyid = ossProperties.getKeyid();
        String keysecret = ossProperties.getKeysecret();
        String bucketname = ossProperties.getBucketname();

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, keyid, keysecret);

        //判断存储空间是否存在
        if(!ossClient.doesBucketExist(bucketname)){
            // 创建存储空间。
            ossClient.createBucket(bucketname);
            //为存储空间设置访问权限
            ossClient.setBucketAcl(bucketname, CannedAccessControlList.PublicRead);
        }

        //文件上传
        String folder = new DateTime().toString("yyyy/MM/dd");
        String fileName = UUID.randomUUID().toString();
        String fileExtention = originalFilename.substring(originalFilename.lastIndexOf("."));

        //module + 2019/12/30 + uuid + 扩展名
        String key = new StringBuffer()
                .append(module)
                .append("/")
                .append(folder)
                .append("/")
                .append(fileName)
                .append(fileExtention)
                .toString();
        ossClient.putObject(bucketname, key, inputStream);


        // 关闭OSSClient。
        ossClient.shutdown();

        return new StringBuffer()
                .append("https://")
                .append(bucketname)
                .append(".")
                .append(endpoint)
                .append("/")
                .append(key)
                .toString();
    }

    @Override
    public void removeFile(String url) {

        String endpoint = ossProperties.getEndpoint();
        String accessKeyId = ossProperties.getKeyid();
        String accessKeySecret = ossProperties.getKeysecret();
        String bucketName = ossProperties.getBucketname();

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        String host = "https://" + bucketName + "." + endpoint + "/";
        String objectName = url.substring(host.length());
        System.out.println(objectName);
        // 删除文件。
        ossClient.deleteObject(bucketName, objectName);

        // 关闭OSSClient。
        ossClient.shutdown();
    }
}
