package com.atguigu.guli.service.vod.controller.Api;

import com.aliyuncs.exceptions.ClientException;
import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.common.base.result.ResultCodeEnum;
import com.atguigu.guli.common.base.util.ExceptionUtils;
import com.atguigu.guli.service.base.exception.GuliException;
import com.atguigu.guli.service.vod.service.VideoService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author zzy
 * @create 2020-01-08 20:52
 */
@Api("阿里云视频播放")
@CrossOrigin //跨域
@RestController
@RequestMapping("/api/vod/video")
@Slf4j
public class ApiVideoController {

    @Autowired
    private VideoService videoService;

    @GetMapping("get-play-auth/{videoSourceId}")
    public R getPalyAuthByVideoSourceId(@PathVariable String videoSourceId) {
        try {
            String playAuth = this.videoService.getVideoPlayAuth(videoSourceId);
            return R.ok().message("获取播放凭证").data("playAuth", playAuth);
        } catch (ClientException e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new GuliException(ResultCodeEnum.FETCH_PLAYAUTH_ERROR);
        }
    }
}