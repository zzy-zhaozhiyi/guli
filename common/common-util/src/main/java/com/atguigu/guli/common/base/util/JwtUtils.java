package com.atguigu.guli.common.base.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

/**
 * @author helen
 * @since 2020/1/11
 */
public class JwtUtils {

    public static final String SUBJECT = "guli-user";

    //秘钥
    public static final String APP_SECRET = "79e7c69681b8270162386e6daa53d1dc";

    //过期时间，毫秒，30分钟
    public static final long EXPIRE = 1000 * 60 * 30;

    /**
     * 生成Jwt令牌
     * @return
     */
    public static String generateJWT(String id, String nickname, String avatar){

        String token = Jwts.builder()
                .setHeaderParam("typ", "JWT") //令牌类型
                .setHeaderParam("alg", "HS256") //签名算法
                .setSubject(SUBJECT) //令牌主题
                .setIssuedAt(new Date()) //签发时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE)) //过期时间
                .claim("id", id)
                .claim("nickname", nickname)
                .claim("avatar", avatar)
                .signWith(SignatureAlgorithm.HS256, APP_SECRET).compact();

        return token;
    }


    /**
     * 校验jwt
     * @param jwtToken
     * @return
     */
    public static Claims checkJWT(String jwtToken){

        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        Claims claims = claimsJws.getBody();

        return claims;
    }
}
