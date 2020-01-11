package com.atguigu.guli.service.ucenter.controller;


import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.service.ucenter.service.MemberService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author zzy
 * @since 2020-01-09
 */
@CrossOrigin
@RestController
@RequestMapping("/admin/ucenter/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @ApiOperation(value = "今日注册数")
    @GetMapping(value = "count-register/{day}")
    public R countRegisterByDay(@PathVariable String day){
        Integer count = memberService.countRegisterByDay(day);
        return R.ok().data("registerCount", count);
    }
}

