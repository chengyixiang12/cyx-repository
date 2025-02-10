package com.soft.base.async;

import com.soft.base.constants.BaseConstant;
import com.soft.base.dto.SaveTmpFileDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.*;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/2/10 9:32
 **/

@Component
@Slf4j
public class FilesTransferAsync {

    @Value(value = "${tmp.path}")
    private String tmpPath;

    @Async
    public void saveTmpFile(SaveTmpFileDto saveTmpFileDto) {
        String filePath = tmpPath + BaseConstant.LEFT_SLASH + saveTmpFileDto.getHashCode() + BaseConstant.LEFT_SLASH + saveTmpFileDto.getIndex() + BaseConstant.TMP_SUFFIX;
        File file = new File(filePath);
        Byte[] byteFile = saveTmpFileDto.getBuffer();
        byte[] buffer = new byte[byteFile.length];

        for (int i = 0; i < byteFile.length; i++) {
            buffer[i] = byteFile[i];
        }

        try (OutputStream os = new FileOutputStream(file)) {
            if (!file.getParentFile().exists()) {
                file.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            os.write(buffer);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
