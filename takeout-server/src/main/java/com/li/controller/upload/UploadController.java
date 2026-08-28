package com.li.controller.upload;

import com.li.common.constant.MessageConstant;
import com.li.common.exception.BaseException;
import com.li.common.result.Result;
import com.li.common.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
public class UploadController {
    @Autowired
    private AliOssUtil aliOssUtil;

    @PostMapping("/admin/common/upload")
    public Result upload(MultipartFile file){
        if (file == null) {
            throw new BaseException(MessageConstant.UPLOAD_FAILED);
        }
        String filename = file.getOriginalFilename(); // 文件名
        String substring = filename.substring(filename.lastIndexOf("."));// 文件后缀
        UUID uuid = UUID.randomUUID();
        String objectName = uuid + substring;
        byte[] bytes = null;
        log.info("上传的文件{}", objectName);
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String url = aliOssUtil.upload(bytes, objectName);
        return Result.success(url);
    }
}
