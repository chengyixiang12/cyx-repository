package com.soft.base.utils;

import com.soft.base.constants.BaseConstant;
import com.soft.base.exception.LocalCacheExpireException;
import com.soft.base.exception.NotSupportTimeUnitException;
import com.soft.base.exception.NumberRangeException;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Author: cyx
 * @Description: 本地缓存
 * @DateTime: 2024/12/7 8:47
 **/
@Component
public class LocalCacheUtil {

    private final Map<String, Storage> LOCAL_CACHE = new ConcurrentHashMap<>();

    /**
     * 存储
     * @param key 键
     * @param value 值
     * @param expire 过期时间；-1：永不过期
     * @param timeUnit 时间单位
     */
    public void store(String key, Object value, long expire, TimeUnit timeUnit) throws NullPointerException, NumberRangeException, NotSupportTimeUnitException {
        if (StringUtils.isBlank(key) || value == null) {
            throw new NullPointerException();
        }
        if (0 > expire) {
            throw new NumberRangeException("过期时间必须大于0");
        }
        Storage storage = new Storage();
        long currentTime = System.currentTimeMillis();
        switch (timeUnit) {
            case SECONDS -> storage.setExpire(currentTime + expire * BaseConstant.LOCAL_CACHE_EXPIRE_SECOND);
            case DAYS -> storage.setExpire(currentTime + expire * BaseConstant.LOCAL_CACHE_EXPIRE_DAY);
            case HOURS -> storage.setExpire(currentTime + expire * BaseConstant.LOCAL_CACHE_EXPIRE_HOURS);
            case MINUTES -> storage.setExpire(currentTime + expire * BaseConstant.LOCAL_CACHE_EXPIRE_MINUTES);
            case MILLISECONDS -> storage.setExpire(currentTime + expire);
            default -> throw new NotSupportTimeUnitException("不支持的时间单位");
        }

        storage.setValue(value);
        LOCAL_CACHE.put(key, storage);
    }

    /**
     * 存储
     * @param key 键
     * @param value 值
     */
    public void store(String key, Object value) throws NullPointerException {
        if (StringUtils.isBlank(key) || value == null) {
            throw new NullPointerException();
        }
        Storage storage = new Storage();
        storage.setExpire(BaseConstant.LOCAL_CACHE_EXPIRE_NEVER);
        storage.setValue(value);
        LOCAL_CACHE.put(key, storage);
    }

    /**
     * 取值
     * @param key
     * @return
     */
    public Object get(String key) throws LocalCacheExpireException {
        Storage storage = LOCAL_CACHE.get(key);
        Long expire = storage.getExpire();
        if (BaseConstant.LOCAL_CACHE_EXPIRE_NEVER.equals(expire) || System.currentTimeMillis() < expire) {
            return storage.getValue();
        } else {
            LOCAL_CACHE.remove(key);
            throw new LocalCacheExpireException(key + "已过期");
        }
    }

    /**
     * 移除
     * @param key
     */
    public void remove(String key) {
        LOCAL_CACHE.remove(key);
    }

    /**
     * 获取所有过期时间
     * @return
     */
    public Map<String, Long> getAllExpire() {
        Map<String, Long> map = new HashMap<>();
        LOCAL_CACHE.forEach((k, v) -> map.put(k, v.getExpire()));
        return map;
    }

    @Data
    private static class Storage {
        private Object value;

        private Long expire;
    }
}
