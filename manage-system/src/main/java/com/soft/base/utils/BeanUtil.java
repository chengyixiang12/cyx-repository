package com.soft.base.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author: cyx
 * @Description: bean转map
 * @DateTime: 2024/11/4 15:14
 **/

@Component
public class BeanUtil {

    /**
     * 将实体类转成map
     * @param bean
     * @return
     */
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

    /**
     * 将map转成实体类
     * @param map
     * @param classType
     * @return
     * @param <T>
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public <T> T map2bean(Map<String,Object> map, Class<T> classType) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        T instance = classType.getConstructor().newInstance();
        Set<String> keys = map.keySet();
        Field[] declaredFields = instance.getClass().getDeclaredFields();
        for (Field c : declaredFields) {
            String name = c.getName();
            if (keys.contains(name) && c.getType().isInstance(map.get(name))) {
                c.setAccessible(true);
                c.set(instance, map.get(name));
            }
        }
        return instance;
    }

}
