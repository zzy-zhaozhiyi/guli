package com.atguigu.guli.service.base.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zzy
 * @create 2020-01-14 11:45
 */
@Data
public class MemberDto implements Serializable {
    private static final long serialVersionUID = 1L;
    //会员id
    private String id;
    //手机号
    private String mobile;
    //昵称
    private String nickname;
}
