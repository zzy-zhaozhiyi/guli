package com.atguigu.guli.service.order.service;

import com.atguigu.guli.service.order.entity.Order;
import com.atguigu.guli.service.order.entity.vo.OrderQueryVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 订单 服务类
 * </p>
 *
 * @author Helen
 * @since 2020-01-14
 */
public interface OrderService extends IService<Order> {

    String saveOrder(String courseId, String memberId);

    Boolean isBuyByCourseId(String courseId, String memberId);

    Map<String,Object> selectPage(Page<Order> pageParam, OrderQueryVo orderQueryVo);

    Order getOrderByOrderNo(String orderNo);

    /**
     * 更改订单状态
     * @param map
     */
    void updateOrderStatus(Map<String, String> map);
}
