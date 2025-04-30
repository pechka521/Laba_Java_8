package com.example.lab8.service;

public class RequestCounter {

    private static long requestCount = 0;

    public static synchronized void increment() {
        requestCount++;
    }

    public static synchronized long getRequestCount() {
        return requestCount;
    }

    public static synchronized void reset() {
        requestCount = 0;
    }
}