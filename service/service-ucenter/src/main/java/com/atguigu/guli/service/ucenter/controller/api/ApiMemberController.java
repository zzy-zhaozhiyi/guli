package com.atguigu.guli.service.ucenter.controller.api;

import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.common.base.util.JwtUtils;
import com.atguigu.guli.service.ucenter.service.MemberService;
import com.atguigu.guli.service.ucenter.vo.LoginInfoVo;
import com.atguigu.guli.service.ucenter.vo.LoginVo;
import com.atguigu.guli.service.ucenter.vo.RegisterVo;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zzy
 * @create 2020-01-12 14:48
 */
@Slf4j
@RestController
@RequestMapping("api/ucenter/member")
@CrossOrigin
public class ApiMemberController {
    @Autowired
    private MemberService memberService;
    @Autowired
    private RedisTemplate redisTemplate;
    @PostMapping("register")
    public R register(@RequestBody RegisterVo registerVo){
        this.memberService.register(registerVo);
        return R.ok().message("註冊成功");

    }
    @PostMapping("login")
    public R login(@RequestBody LoginVo loginVo){
      String jwt=  this.memberService.login(loginVo);
      return R.ok().message("登录成功").data("token",jwt);
    }
    @GetMapping("auth/get-login-info")
    public R getLoginInfo(HttpServletRequest request){
        //从请求头中获取cookie信息
        String token = request.getHeader("token");
        LoginInfoVo loginInfoVo= this.memberService.getLoginInfo(token);
        return R.ok().data("item",loginInfoVo);


    }
}
