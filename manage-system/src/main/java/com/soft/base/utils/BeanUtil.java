package com.soft.base.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: cyx
 * @Description: bean转map
 * @DateTime: 2024/11/4 15:14
 **/

@Component
public class BeanUtil {

    public Map<String,Object> bean2map(Object bean) {
        Map<String,Object> map = new HashMap<>();
        try {
            for (PropertyDescriptor propertyDescriptor : BeanUtils.getPropertyDescriptors(bean.getClass())) {
                String key = propertyDescriptor.getName();
                Object value = propertyDescriptor.getReadMethod().invoke(bean);
                map.put(key, value);
            }
            return map;
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
