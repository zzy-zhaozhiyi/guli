package com.atguigu.guli.service.sms.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zzy
 * @create 2020-01-12 11:47
 */
@Component
@ConfigurationProperties(prefix = "aliyun.sms")
@Data
public class SmsProperties {
    private String regionid;
    private String keyid;
    private String keysecret;
    private String templatecode;
    private String signname;
}
