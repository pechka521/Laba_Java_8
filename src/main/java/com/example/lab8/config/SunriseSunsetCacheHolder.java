package com.example.lab8.config;

import com.example.lab8.model.SunriseSunset;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SunriseSunsetCacheHolder {

    private static final Map<String, List<SunriseSunset>> INSTANCE = new HashMap<>();

    public static Map<String, List<SunriseSunset>> getInstance() {
        return INSTANCE;
    }
}