package com.atguigu.guli.service.order.service.impl;

import com.atguigu.guli.service.base.dto.CourseDto;
import com.atguigu.guli.service.base.dto.MemberDto;
import com.atguigu.guli.service.order.client.CourseFeignClient;
import com.atguigu.guli.service.order.client.MemberFeignClient;
import com.atguigu.guli.service.order.entity.Order;
import com.atguigu.guli.service.order.entity.PayLog;
import com.atguigu.guli.service.order.entity.vo.OrderQueryVo;
import com.atguigu.guli.service.order.mapper.OrderMapper;
import com.atguigu.guli.service.order.mapper.PayLogMapper;
import com.atguigu.guli.service.order.service.OrderService;
import com.atguigu.guli.service.order.util.OrderNoUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单 服务实现类
 * </p>
 *
 * @author Helen
 * @since 2020-01-14
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private CourseFeignClient courseFeignClient;

    @Autowired
    private MemberFeignClient memberFeignClient;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private PayLogMapper payLogMapper;

    @Override
    public String saveOrder(String courseId, String memberId) {

        CourseDto courseDto = courseFeignClient.getCourseDtoByCourseId(courseId);
        MemberDto memberDto = memberFeignClient.getMemberDtoByMemberId(memberId);

        Order order = new Order();

        order.setOrderNo(OrderNoUtils.getOrderNo());
        order.setCourseId(courseId);
        order.setCourseCover(courseDto.getCover());
        order.setCourseTitle(courseDto.getTitle());
        order.setTeacherName(courseDto.getTeacherName());
        order.setTotalFee(courseDto.getPrice());

        order.setMemberId(memberId);
        order.setMobile(memberDto.getMobile());
        order.setNickname(memberDto.getNickname());

        order.setStatus(0);
        order.setPayType(1);

        baseMapper.insert(order);

        return order.getId();
    }

    @Override
    public Boolean isBuyByCourseId(String courseId, String memberId) {

        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("member_id", memberId)
                .eq("course_id", courseId)
                .eq("status", 1);

        Integer count = orderMapper.selectCount(queryWrapper);
        return count.intValue() > 0;
    }

    @Override
    public Map<String, Object> selectPage(Page<Order> pageParam, OrderQueryVo orderQueryVo) {

        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("gmt_create");

        String memberId = orderQueryVo.getMemberId();
        Integer status = orderQueryVo.getStatus();
        String gmtCreateBegin = orderQueryVo.getGmtCreateBegin();
        String gmtCreateEnd = orderQueryVo.getGmtCreateEnd();

        if(!StringUtils.isEmpty(memberId)) {
            queryWrapper.eq("member_id", memberId);
        }

        if (status != null) {
            queryWrapper.eq("status", status);
        }

        if (!StringUtils.isEmpty(gmtCreateBegin)) {
            queryWrapper.ge("gmt_create", gmtCreateBegin);
        }

        if (!StringUtils.isEmpty(gmtCreateEnd)) {
            queryWrapper.le("gmt_create", gmtCreateEnd);
        }

        baseMapper.selectPage(pageParam, queryWrapper);

        List<Order> records = pageParam.getRecords();
        long current = pageParam.getCurrent();
        long pages = pageParam.getPages();
        long size = pageParam.getSize();
        long total = pageParam.getTotal();
        boolean hasNext = pageParam.hasNext();
        boolean hasPrevious = pageParam.hasPrevious();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("items", records);
        map.put("current", current);
        map.put("pages", pages);
        map.put("size", size);
        map.put("total", total);
        map.put("hasNext", hasNext);
        map.put("hasPrevious", hasPrevious);

        return map;

    }

    @Override
    public Order getOrderByOrderNo(String orderNo) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", orderNo);
        return orderMapper.selectOne(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateOrderStatus(Map<String, String> map) {

        //更新本地课程订单状态
        String orderNo = map.get("out_trade_no");
        Order order = this.getOrderByOrderNo(orderNo);
        order.setStatus(1);
        baseMapper.updateById(order);

        //记录支付日志
        PayLog payLog = new PayLog();
        payLog.setOrderNo(orderNo);
        payLog.setPayTime(new Date());
        payLog.setPayType(1);//支付类型
        payLog.setTotalFee(order.getTotalFee().longValue());//总金额(分)
        payLog.setTradeState(map.get("trade_state"));//支付状态
        payLog.setTransactionId(map.get("transaction_id"));
        payLog.setAttr(new Gson().toJson(map));
        payLogMapper.insert(payLog);//插入到支付日志表

        //更改课程购买数量
        //TODO
    }
}
