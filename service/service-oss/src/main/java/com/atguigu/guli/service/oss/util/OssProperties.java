package com.atguigu.guli.service.oss.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author helen
 * @since 2019/12/30
 */
@Data
@Component
@ConfigurationProperties(prefix = "aliyun.oss.file")
public class OssProperties {

    private String endpoint;

    private String keyid;

    private String keysecret;

    private String bucketname;

}
