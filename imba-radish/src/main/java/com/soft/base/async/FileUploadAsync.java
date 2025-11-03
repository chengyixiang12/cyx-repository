package com.soft.base.async;

import com.soft.base.constants.BaseConstant;
import com.soft.base.model.dto.SaveTmpFileDto;
import com.soft.base.utils.MinioUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/2/10 9:32
 **/

@Component
@Slf4j
@RequiredArgsConstructor
public class FileUploadAsync {

    public final MinioUtil minioUtil;

    @Async
    public void saveTmpFile(SaveTmpFileDto saveTmpFileDto) {
    }
}
