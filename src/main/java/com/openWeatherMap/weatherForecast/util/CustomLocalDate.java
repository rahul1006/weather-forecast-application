package com.openWeatherMap.weatherForecast.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

@Component
public class CustomLocalDate {

    @Autowired
    private Clock clock;

    public LocalDateTime getCurrentDate() {
        return LocalDateTime.now(clock);
    }
}
