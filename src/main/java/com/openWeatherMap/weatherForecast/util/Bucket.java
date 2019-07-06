package com.openWeatherMap.weatherForecast.util;

import com.openWeatherMap.weatherForecast.domain.openweathermap.WeatherMetrics;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class Bucket {

    private List<Integer> hours;
    private List<WeatherMetrics> elements;

    public List<Integer> getHours() {
        if (CollectionUtils.isEmpty(this.hours)) {
            this.hours = new ArrayList<>();
        }
        return hours;
    }

    public void setHours(List<Integer> hours) {
        this.hours = hours;
    }

    public List<WeatherMetrics> getElements() {
        if (CollectionUtils.isEmpty(this.elements)) {
            this.elements = new ArrayList<>();
        }
        return elements;
    }

    public void setElements(List<WeatherMetrics> elements) {
        this.elements = elements;
    }

    public boolean hasValidHour(int hour) {
        return this.getHours().contains(hour);
    }

    public void setValidHours(int startHour, int duration) {
        for (int i = startHour; i < startHour + duration; i++) {
            if (i < 24) {
                this.getHours().add(i);
            } else {
                this.getHours().add(i - 24);
            }
        }
    }
}


