package com.soft.base.conf;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.nio.charset.Charset;
import java.time.LocalDateTime;

import static com.soft.base.constants.BaseConstant.DEL_FLAG_EXIST;

@Configuration
public class MybatisPlusAutoConfig implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        // 自动填充创建时间
        fillValIfNullByName("createTime", LocalDateTime.now(),metaObject);
        // 自动填充创建人
        fillValIfNullByName("createBy", getCurrentUsername(), metaObject);
        // 自动填充更新时间
        fillValIfNullByName("updateTime", LocalDateTime.now(), metaObject);
        // 自动填充更新人
        fillValIfNullByName("updateBy", getCurrentUsername(), metaObject);
        // 自动填充逻辑删除
        fillValIfNullByName("delFlag", DEL_FLAG_EXIST, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 自动填充更新时间
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        // 自动填充更新人
        this.setFieldValByName("updateBy", getCurrentUsername(), metaObject);
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getPrincipal() instanceof String) {
                return null;
            }
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userDetails.getUsername(); // 返回当前用户的用户名
        }
        return null; // 或者返回一个默认值
    }

    /**
     * 如果提前设置了值，则不自动插入
     * @param fieldName
     * @param fieldVal
     * @param metaObject
     */
    private static void fillValIfNullByName(String fieldName, Object fieldVal, MetaObject metaObject) {
        Object userSetValue = metaObject.getValue(fieldName);
        String setValueStr = StrUtil.str(userSetValue, Charset.defaultCharset());
        if (StrUtil.isNotBlank(setValueStr)) {
            return;
        }
        metaObject.setValue(fieldName, fieldVal);
    }
}
