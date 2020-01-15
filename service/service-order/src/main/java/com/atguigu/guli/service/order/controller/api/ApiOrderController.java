package com.atguigu.guli.service.order.controller.api;

import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.common.base.util.JwtUtils;
import com.atguigu.guli.service.order.entity.Order;
import com.atguigu.guli.service.order.entity.vo.OrderQueryVo;
import com.atguigu.guli.service.order.service.OrderService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author zzy
 * @since 2020/1/14
 */
@RestController
@RequestMapping("/api/order")
public class ApiOrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 下订单
     * @param courseId
     * @param request
     * @return
     */
    @PostMapping("auth/save/{courseId}")
    public R save(@PathVariable String courseId, HttpServletRequest request){
        String jwtToken = request.getHeader("token");
        //从token中解析出用户id
        String memberId = JwtUtils.getMemberIdByJwtToken(jwtToken);
        String orderId = orderService.saveOrder(courseId, memberId);
        return R.ok().data("orderId", orderId);
    }


    @GetMapping("auth/is-buy/{courseId}")
    public R isBuyByCourseId(@PathVariable String courseId, HttpServletRequest request){
        String jwtToken = request.getHeader("token");
        //从token中解析出用户id
        String memberId = JwtUtils.getMemberIdByJwtToken(jwtToken);
        Boolean isBuy = orderService.isBuyByCourseId(courseId, memberId);
        return R.ok().data("isBuy", isBuy);
    }

    @GetMapping("auth/get/{orderId}")
    public R get(@PathVariable String orderId){

        Order order = orderService.getById(orderId);
        return R.ok().data("item", order);
    }

    @ApiOperation(value = "删除订单")
    @DeleteMapping("auth/remove/{orderId}")
    public R remove(@PathVariable String orderId) {
        orderService.removeById(orderId);
        return R.ok();
    }

    @ApiOperation(value = "获取订单分页列表")
    @GetMapping("auth/{page}/{limit}")
    public R index(@PathVariable Long page, @PathVariable Long limit, OrderQueryVo orderQueryVo, HttpServletRequest request) {

        String jwtToken = request.getHeader("token");
        String memberId = JwtUtils.getMemberIdByJwtToken(jwtToken);

        orderQueryVo.setMemberId(memberId);
        Page<Order> pageParam = new Page<>(page, limit);
        Map<String, Object> map = orderService.selectPage(pageParam, orderQueryVo);
        return R.ok().data(map);
    }
}
