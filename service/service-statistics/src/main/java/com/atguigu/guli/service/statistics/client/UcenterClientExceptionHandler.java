package com.atguigu.guli.service.statistics.client;

import com.atguigu.guli.common.base.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author zzy
 * @create 2020-01-10 11:09
 */
@Slf4j
@Component
public class UcenterClientExceptionHandler implements  UcenterClient {

    @Override
    public R registerCount(String day) {

        //错误日志
        log.error("熔断器被执行");
        return R.ok().data("countRegister", 0);
    }
}
