package com.openWeatherMap.weatherForecast.domain.openweathermap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.openWeatherMap.weatherForecast.constants.WeatherConstant;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherMetrics {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(WeatherConstant.DATE_FORMAT);

    private LocalDateTime start_date;
    private String dt_txt;
    private String start_date_txt;
    private Main main;

    public LocalDateTime getStart_date() {
        return start_date;
    }

    public void setStart_date(LocalDateTime start_date) {
        this.start_date = start_date;
    }


    public String getDt_txt() {
        return dt_txt;
    }

    public void setDt_txt(String dt_txt) {
        this.dt_txt = dt_txt;
        if (this.dt_txt != null) {
            this.start_date = LocalDateTime.from(FORMATTER.parse(this.dt_txt));
            this.start_date_txt = dt_txt.split("\\s+")[0];
        }
    }

    public String getStart_date_txt() {
        return start_date_txt;
    }

    public void setStart_date_txt(String start_date_txt) {
        this.start_date_txt = start_date_txt;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }
}