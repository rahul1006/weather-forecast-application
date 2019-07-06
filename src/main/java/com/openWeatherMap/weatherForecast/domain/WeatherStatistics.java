package com.openWeatherMap.weatherForecast.domain;

import java.util.LinkedHashMap;
import java.util.Map;

public class WeatherStatistics {

    private String date;
    private Map<String, Metrics> metrics;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Map<String, Metrics> getMetrics() {
        if (metrics == null) {
            metrics = new LinkedHashMap<>();
        }
        return metrics;
    }

    public void setMetrics(Map<String, Metrics> averageStatistics) {
        this.metrics = averageStatistics;
    }
}