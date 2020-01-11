package com.atguigu.guli.service.ucenter.service.impl;

import com.atguigu.guli.service.ucenter.entity.Member;
import com.atguigu.guli.service.ucenter.mapper.MemberMapper;
import com.atguigu.guli.service.ucenter.service.MemberService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 会员表 服务实现类
 * </p>
 *
 * @author zzy
 * @since 2020-01-09
 */
@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {
@Autowired
private MemberMapper memberMapper;
    @Override
    public Integer countRegisterByDay(String day) {
        Integer register = this.memberMapper.countRegisterByDay(day);
        return register;
    }
}
