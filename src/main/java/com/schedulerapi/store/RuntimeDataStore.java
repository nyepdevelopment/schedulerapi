package com.schedulerapi.store;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class RuntimeDataStore {
    private final Map<String, List<String>> dataMap = new HashMap<>();

    public void setData(String key, List<String> value) {
        dataMap.put(key, value);
    }

    public List<String> getData(String key) {
        return dataMap.get(key);
    }

    public void removeData(String key) {
        dataMap.remove(key);
    }
}
