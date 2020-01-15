package com.atguigu.guli.service.order.client;

import com.atguigu.guli.service.base.dto.MemberDto;
import com.atguigu.guli.service.order.client.exception.MemberFeignClientExceptionHandler;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author helen
 * @since 2020/1/14
 */
@FeignClient(value = "guli-ucenter", fallback = MemberFeignClientExceptionHandler.class)
public interface MemberFeignClient {

    @GetMapping("/api/ucenter/member/inner/get-member-dto/{memberId}")
    public MemberDto getMemberDtoByMemberId(@PathVariable(value = "memberId") String memberId);
}
