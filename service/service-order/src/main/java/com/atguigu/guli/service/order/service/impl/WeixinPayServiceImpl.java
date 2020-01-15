package com.atguigu.guli.service.order.service.impl;

import com.atguigu.guli.common.base.result.ResultCodeEnum;
import com.atguigu.guli.common.base.util.ExceptionUtils;
import com.atguigu.guli.service.base.exception.GuliException;
import com.atguigu.guli.service.order.entity.Order;
import com.atguigu.guli.service.order.service.OrderService;
import com.atguigu.guli.service.order.service.WeixinPayService;
import com.atguigu.guli.service.order.util.HttpClient;
import com.atguigu.guli.service.order.util.WeixinPayProperties;
import com.github.wxpay.sdk.WXPayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author helen
 * @since 2020/1/15
 */
@Service
@Slf4j
public class WeixinPayServiceImpl implements WeixinPayService {


    @Autowired
    private WeixinPayProperties weixinPayProperties;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private OrderService orderService;

    @Override
    public Map<String, Object> createNative(String orderNo, String remoteAddr) {
        try {

            //检查redis中是否有订单数据
            Map<String, Object> payMap = (Map)redisTemplate.opsForValue().get(orderNo);
            if(payMap != null){
                return payMap;
            }

            //根据课程订单号获取订单
            Order order = orderService.getOrderByOrderNo(orderNo);

            //调用微信api接口：统一下单（支付订单）
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");

            //组装接口参数
            Map<String, String> map = new HashMap<>();
            map.put("appid", weixinPayProperties.getAppid());//关联的公众号的appid
            map.put("mch_id", weixinPayProperties.getPartner());//商户号
            map.put("nonce_str", WXPayUtil.generateNonceStr());//生成随机字符串
            map.put("body", order.getCourseTitle());
            map.put("out_trade_no", orderNo);

            //注意，这里必须使用字符串类型的参数
            String totalFee = order.getTotalFee().multiply(new BigDecimal("100")).intValue() + "";
            map.put("total_fee", totalFee);

            map.put("spbill_create_ip", remoteAddr);
            map.put("notify_url", weixinPayProperties.getNotifyurl());
            map.put("trade_type", "NATIVE");

            //将参数转换成xml字符串格式
            String xmlParams = WXPayUtil.generateSignedXml(map, weixinPayProperties.getPartnerkey());

            System.out.println("xmlParams：" + xmlParams);

            client.setXmlParam(xmlParams);
            client.setHttps(true);
            //发送请求
            client.post();

            //得到响应结果
            String xml = client.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);

            System.out.println("xml: " + xml);
            System.out.println("return_code: " + resultMap.get("return_code"));
            System.out.println("return_msg: " + resultMap.get("return_msg"));

            System.out.println("result_code: " + resultMap.get("result_code"));
            System.out.println("err_code: " + resultMap.get("err_code"));
            System.out.println("err_code_des: " + resultMap.get("err_code_des"));

            System.out.println("code_url：" + resultMap.get("code_url"));


            if("FAIL".equals(resultMap.get("return_code")) || "FAIL".equals(resultMap.get("result_code"))){
                log.error("微信支付统一下单错误 - "
                        + "return_code: " + resultMap.get("return_code")
                        + "return_msg: " + resultMap.get("return_msg")
                        + "result_code: " + resultMap.get("result_code")
                        + "err_code: " + resultMap.get("err_code")
                        + "err_code_des: " + resultMap.get("err_code_des"));

                throw new GuliException(ResultCodeEnum.PAY_UNIFIEDORDER_ERROR);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("result_code", resultMap.get("result_code"));
            result.put("code_url", resultMap.get("code_url"));
            result.put("course_id", order.getCourseId());
            result.put("total_fee", order.getTotalFee());
            result.put("out_trade_no", orderNo);

            //微信支付二维码2小时过期，可以存入redis中
            redisTemplate.opsForValue().set(orderNo, result, 120, TimeUnit.MINUTES);

            return result;

        } catch (Exception e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new GuliException(ResultCodeEnum.PAY_UNIFIEDORDER_ERROR);
        }
    }

    @Override
    public Map<String, String> queryPayStatus(String orderNo) {

        try {
            //调用微信api接口：查询订单接口
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");

            //组装参数
            Map<String, String> map = new HashMap<>();
            map.put("appid", weixinPayProperties.getAppid());
            map.put("mch_id", weixinPayProperties.getPartner());
            map.put("out_trade_no", orderNo);
            map.put("nonce_str", WXPayUtil.generateNonceStr());


            //将参数转换成xml字符串格式
            String xmlParams = WXPayUtil.generateSignedXml(map, weixinPayProperties.getPartnerkey());
            System.out.println("xmlParams：" + xmlParams);

            client.setXmlParam(xmlParams);
            client.setHttps(true);
            //发送请求
            client.post();

            //得到响应结果
            String xml = client.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);

            if("FAIL".equals(resultMap.get("return_code")) || "FAIL".equals(resultMap.get("result_code"))){
                log.error("查询支付结果错误 - "
                        + "return_code: " + resultMap.get("return_code")
                        + "return_msg: " + resultMap.get("return_msg")
                        + "result_code: " + resultMap.get("result_code")
                        + "err_code: " + resultMap.get("err_code")
                        + "err_code_des: " + resultMap.get("err_code_des"));

                throw new GuliException(ResultCodeEnum.PAY_ORDERQUERY_ERROR);
            }

            return resultMap;

        } catch (Exception e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new GuliException(ResultCodeEnum.PAY_ORDERQUERY_ERROR);
        }

    }
}
