package com.openWeatherMap.weatherForecast.domain.openweathermap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherForecast {

    @JsonProperty("cod")
    private String cod;

    @JsonProperty("message")
    private String message;

    @JsonProperty("list")
    private List<WeatherMetrics> weatherData = new ArrayList<>();

    public List<WeatherMetrics> getWeatherData() {
        return weatherData;
    }

    public void setWeatherData(List<WeatherMetrics> weatherData) {
        this.weatherData = weatherData;
    }

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}