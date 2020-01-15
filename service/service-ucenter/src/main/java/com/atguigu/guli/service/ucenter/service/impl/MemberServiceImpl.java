package com.atguigu.guli.service.ucenter.service.impl;

import com.atguigu.guli.common.base.result.ResultCodeEnum;
import com.atguigu.guli.common.base.util.FormUtils;
import com.atguigu.guli.common.base.util.GuliAppUtils;
import com.atguigu.guli.common.base.util.JwtUtils;
import com.atguigu.guli.common.base.util.MD5;
import com.atguigu.guli.service.base.dto.MemberDto;
import com.atguigu.guli.service.base.exception.GuliException;
import com.atguigu.guli.service.ucenter.entity.Member;
import com.atguigu.guli.service.ucenter.mapper.MemberMapper;
import com.atguigu.guli.service.ucenter.service.MemberService;
import com.atguigu.guli.service.ucenter.vo.LoginInfoVo;
import com.atguigu.guli.service.ucenter.vo.LoginVo;
import com.atguigu.guli.service.ucenter.vo.RegisterVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Integer countRegisterByDay(String day) {
        Integer register = this.memberMapper.countRegisterByDay(day);
        return register;
    }

    @Override
    public void register(RegisterVo registerVo) {
        //先判断传入的参数是否合适
        String code = registerVo.getCode();
        String mobile = registerVo.getMobile();
        String nickname = registerVo.getNickname();
        String password = registerVo.getPassword();

        if (StringUtils.isEmpty(mobile) ||
                !FormUtils.isMobile(mobile) ||
                StringUtils.isEmpty(code) ||
                StringUtils.isEmpty(nickname) ||
                StringUtils.isEmpty(password)) {
            throw new GuliException(ResultCodeEnum.PARAM_ERROR);
        }
        //比较出入的验证码和发送的验证码比对
        String s = (String) this.redisTemplate.opsForValue().get(GuliAppUtils.PHONE_NUMBER + mobile);

        if (!s.equals(code)) {
            throw new GuliException(ResultCodeEnum.CODE_ERROR);

        }
        //验证该用户手机号是否已经被注册
        QueryWrapper<Member> queryWrapper = new QueryWrapper<Member>().eq("mobile", mobile);
        Integer count = this.baseMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new GuliException(ResultCodeEnum.REGISTER_MOBLE_ERROR);
        }
//完成以上的步骤后进行註冊保存
        Member member = new Member();
        member.setMobile(mobile);
        member.setNickname(nickname);
        member.setPassword(MD5.encrypt(password));//加密进行存入
        member.setDisabled(false);//註冊不禁用
        member.setAvatar("D:\\workspace_idea0722\\beautiful.jpg");
        this.baseMapper.insert(member);

    }

    @Override
    public String login(LoginVo loginVo) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();//未加密的用户登录密码

        if (StringUtils.isEmpty(mobile)||StringUtils.isEmpty(password)){
            throw new GuliException(ResultCodeEnum.PARAM_ERROR);
        }
         //先根据账户名查询数据库，在比对密码是否一致(要相互转化下，数据库的已加密)
        QueryWrapper<Member> queryWrapper = new QueryWrapper<Member>().eq("mobile", mobile);
        Member member = this.baseMapper.selectOne(queryWrapper);
        if (member == null) {
            throw new GuliException(ResultCodeEnum.LOGIN_MOBLE_ERROR);
        }
        String password1 = member.getPassword();
       if(!password1.equals(MD5.encrypt(password))){
           throw  new GuliException(ResultCodeEnum.LOGIN_PASSWORD_ERROR);
       }
        //检验用户是否被警用
        if(member.getDisabled()){
            throw new GuliException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
        }
      //登录成功后进行生成jwt返回
        String jwt = JwtUtils.generateJWT(member.getId(), member.getNickname(), member.getAvatar());
        return jwt;
    }

    @Override
    public LoginInfoVo getLoginInfo(String token) {
        Claims claims = JwtUtils.checkJWT(token);
        String id = (String) claims.get("id");
        String awatar = (String) claims.get("avatar");
        String nickname = (String) claims.get("nickname");

        LoginInfoVo vo = new LoginInfoVo();
        vo.setAvatar(awatar);
        vo.setId(id);
        vo.setNickname(nickname);

        return vo;
    }

    @Override
    public Member getByOpenid(String openid) {
        QueryWrapper<Member> queryWrapper = new QueryWrapper<Member>().eq("openid", openid);
        Member member = this.baseMapper.selectOne(queryWrapper);
        return member;
    }

    @Override
    public MemberDto getMemberDtoByMemberId(String memberId) {
        Member member = this.baseMapper.selectById(memberId);
        MemberDto memberDto = new MemberDto();
        BeanUtils.copyProperties(member,memberDto);
        return memberDto;
    }
}
