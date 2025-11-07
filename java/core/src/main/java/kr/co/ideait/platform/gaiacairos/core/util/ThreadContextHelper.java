package kr.co.ideait.platform.gaiacairos.core.util;


import org.apache.commons.collections.MapUtils;

import java.util.HashMap;
import java.util.Map;

public class ThreadContextHelper {
    private final ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<>();

    public Map<String, Object> getThreadLocal() {
        Map<String, Object> cacheMap = threadLocal.get();

        if (MapUtils.isEmpty(cacheMap)) {
            cacheMap =  new HashMap<>();
        }

        return cacheMap;
    }

    public Object get(String key) {
        Map<String, Object> cacheMap = getThreadLocal();

        return cacheMap.get(key);
    }

    public void set(String key, Object value) {
        Map<String, Object> cacheMap = getThreadLocal();
        cacheMap.put(key, value);

        threadLocal.set(cacheMap);
    }

    public void clear() {
        threadLocal.remove();
    }
}
