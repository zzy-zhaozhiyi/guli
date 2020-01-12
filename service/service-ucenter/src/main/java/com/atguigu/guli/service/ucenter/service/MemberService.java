package com.atguigu.guli.service.ucenter.service;

import com.atguigu.guli.service.ucenter.entity.Member;
import com.atguigu.guli.service.ucenter.vo.LoginInfoVo;
import com.atguigu.guli.service.ucenter.vo.LoginVo;
import com.atguigu.guli.service.ucenter.vo.RegisterVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 会员表 服务类
 * </p>
 *
 * @author zzy
 * @since 2020-01-09
 */
public interface MemberService extends IService<Member> {

    Integer countRegisterByDay(String day);

    void register(RegisterVo registerVo);

    String login(LoginVo loginVo);

    LoginInfoVo getLoginInfo(String token);
}
