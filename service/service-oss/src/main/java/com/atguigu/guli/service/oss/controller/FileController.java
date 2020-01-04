package com.atguigu.guli.service.oss.controller;

import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.common.base.result.ResultCodeEnum;
import com.atguigu.guli.common.base.util.ExceptionUtils;
import com.atguigu.guli.service.base.exception.GuliException;
import com.atguigu.guli.service.oss.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author helen
 * @since 2019/12/30
 */
@RestController
@RequestMapping("/admin/oss/file")
@Api( "阿里云文件管理")
@CrossOrigin
@Slf4j
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("upload")
    public R upload(@RequestParam("file") MultipartFile file,@RequestParam("module") String module)  {//TODO 优化为自定义异常

        try {
            InputStream inputStream = file.getInputStream();
            String originalFilename = file.getOriginalFilename();
            String upload = fileService.upload(inputStream, module, originalFilename);
            String url = upload;

            return R.ok().message("文件上传成功").data("url", url);
        } catch (IOException e) {
            //添加异常跟踪信息
            log.error(ExceptionUtils.getMessage(e));
            throw new GuliException(ResultCodeEnum.FILE_UPLOAD_ERROR);
        }
    }
    @ApiOperation(value = "文件删除")
    @DeleteMapping("remove")
    public R removeFile(@ApiParam(name = "url", value = "要删除的文件路径", required = true) @RequestBody String url) {

        try {
            fileService.removeFile(url);
            return R.ok().message("文件刪除成功");
        } catch (Exception e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new GuliException(ResultCodeEnum.FILE_DELETE_ERROR);
        }
    }
}
