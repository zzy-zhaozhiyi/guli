package com.atguigu.guli.service.sms.service;

import java.util.Map;

/**
 * @author helen
 * @since 2020/1/12
 */
public interface SmsService {

    void send(String phone, Map<String, Object> param);
}
