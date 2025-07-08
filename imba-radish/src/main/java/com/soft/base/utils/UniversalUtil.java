package com.soft.base.utils;

import com.soft.base.constants.BaseConstant;
import com.soft.base.exception.GlobalException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.List;
import java.util.UUID;

/**
 * @Author: cyx
 * @Description: 通用工具类
 * @DateTime: 2024/11/7 17:49
 **/

@Component
@Valid
public class UniversalUtil {

    /**
     * 生成随机数
     *
     * @param length
     * @return
     */
    public String generate(Integer length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            double random = Math.random() * 10;
            sb.append((int) random);
        }
        return sb.toString();
    }

    /**
     * list转数组
     *
     * @param list
     * @param valueType
     * @param <T>
     * @return
     */
    public <T> T[] toArray(@NotEmpty List<T> list, @NotNull Class<T[]> valueType) {
        if (valueType == null || !valueType.isArray()) {
            throw new GlobalException("valueType必须是数组类型");
        }
        Class<?> componentType = valueType.getComponentType();
        if (componentType == null) {
            throw new GlobalException("valueType必须是数组类型");
        }
        if (list == null) {
            throw new GlobalException("list不能为空");
        }
        @SuppressWarnings("unchecked")
        T[] array = (T[]) Array.newInstance(componentType, list.size());
        for (int i = 0; i < list.size(); i++) {
            Array.set(array, i, list.get(i));
        }
        return array;
    }

    /**
     * 生成唯一key
     * @return
     */
    public String fileKeyGen() {
        return UUID.randomUUID().toString().replaceAll(BaseConstant.ENG_DASH, BaseConstant.BLANK_CHARACTER);
    }
}
