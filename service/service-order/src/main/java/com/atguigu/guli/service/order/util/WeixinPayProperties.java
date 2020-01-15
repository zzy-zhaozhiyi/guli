package com.atguigu.guli.service.order.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author helen
 * @since 2020/1/15
 */
@Data
@Component
@ConfigurationProperties(prefix="weixin.pay")
public class WeixinPayProperties {
    private String appid;
    private String partner;
    private String partnerkey;
    private String notifyurl;
}
