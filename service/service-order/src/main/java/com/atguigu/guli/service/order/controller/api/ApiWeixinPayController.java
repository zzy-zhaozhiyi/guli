package com.atguigu.guli.service.order.controller.api;

import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.common.base.result.ResultCodeEnum;
import com.atguigu.guli.service.order.service.OrderService;
import com.atguigu.guli.service.order.service.WeixinPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author helen
 * @since 2020/1/15
 */
@RequestMapping("/api/order/weixinpay")
@RestController
public class ApiWeixinPayController {

    @Autowired
    private WeixinPayService weixinPayService;

    @Autowired
    private OrderService orderService;

    @GetMapping("create-native/{orderNo}")
    public R createNative(@PathVariable String orderNo, HttpServletRequest request){

        String remoteAddr = request.getRemoteAddr();
        Map<String, Object> map = weixinPayService.createNative(orderNo, remoteAddr);
        return R.ok().data(map);
    }

    /**
     * 在前端通过javascript定时器调用此接口
     * @param orderNo
     * @return
     */
    @GetMapping("query-pay-status/{orderNo}")
    public R queryPayStatus(@PathVariable String orderNo){
        //调用查询订单状态的接口
        Map<String, String> map = weixinPayService.queryPayStatus(orderNo);
        if("SUCCESS".equals(map.get("trade_state"))){
            orderService.updateOrderStatus(map);
            return R.ok().message("支付成功");
        }
        return R.setResult(ResultCodeEnum.PAY_RUN);
    }


}
