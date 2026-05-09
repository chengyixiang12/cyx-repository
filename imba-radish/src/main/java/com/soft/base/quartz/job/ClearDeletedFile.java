package com.soft.base.quartz.job;

import cn.hutool.core.collection.CollectionUtil;
import com.soft.base.model.dto.SelectDeletedFileDto;
import com.soft.base.service.SysFileService;
import com.soft.base.utils.MinioUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author cyq
 * @date 2025/11/1
 * @description
 */
@Component
@Slf4j
public class ClearDeletedFile implements Job {

    private SysFileService sysFileService;

    private MinioUtil minioUtil;

    @Autowired
    public void setSysFileService(SysFileService sysFileService) {
        this.sysFileService = sysFileService;
    }

    @Autowired
    public void setMinioUtil(MinioUtil minioUtil) {
        this.minioUtil = minioUtil;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        List<SelectDeletedFileDto> selectDeletedFileDtoList = sysFileService.selectDeletedFiles();
        log.info("本次共计清理{}个文件", selectDeletedFileDtoList.size());
        selectDeletedFileDtoList.forEach(item -> {
            if (item.getRetain()) {
                // 因其它文件数据引用了该文件，所以保留
                return;
            }
            minioUtil.delete(item.getBucket(), item.getObjectKey());
        });
        List<Long> list = selectDeletedFileDtoList.stream().map(SelectDeletedFileDto::getId).toList();
        if (CollectionUtil.isNotEmpty(list)) {
            sysFileService.deleteRealByIds(list);
            log.info("清理完毕");
        }
    }
}
