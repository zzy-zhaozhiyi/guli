package com.atguigu.guli.service.order.service;

import java.util.Map;

/**
 * @author helen
 * @since 2020/1/15
 */
public interface WeixinPayService {

    /**
     * 根据订单号生成支付链接
     * @param orderNo
     * @param remoteAddr
     * @return
     */
    Map<String, Object> createNative(String orderNo, String remoteAddr);

    /**
     * 根据订单号去微信服务器查询支付状态
     * @param orderNo
     * @return
     */
    Map<String, String> queryPayStatus(String orderNo);
}
