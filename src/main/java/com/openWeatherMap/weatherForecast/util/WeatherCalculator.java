package com.openWeatherMap.weatherForecast.util;

import com.openWeatherMap.weatherForecast.domain.Metrics;
import com.openWeatherMap.weatherForecast.domain.WeatherStatistics;
import com.openWeatherMap.weatherForecast.domain.openweathermap.WeatherMetrics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class WeatherCalculator {

    private final CustomLocalDate customLocalDate;

    @Autowired
    public WeatherCalculator(CustomLocalDate customLocalDate) {
        this.customLocalDate = customLocalDate;
    }

    /**
     * Round
     * This Method Round a Double Number to N Decimal Places.
     *
     * @param value
     * @param places
     * @return Double
     */

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * get Weather Statistics
     * THis Method calculate the weather statistics and return the list of statistics
     *
     * @param list            (List of the whether data received form open weather map API service)
     * @param startHour       (Staring hour of day to calculate whether metrics. i.e 6)
     * @param duration        (Interval on which the metrics should be calculated. i.e at 12 hour)
     * @param forecastDays    (Number of the days for which whether forecast metrics are required)
     * @param statisticGroups (Array of the property against which metrics value will be displayed. i.e. output json property name)
     * @return List<WeatherStatistics>
     */

    public List<WeatherStatistics> getWeatherStatistics(List<WeatherMetrics> list, int startHour, int duration, int forecastDays, String[] statisticGroups) {

        Map<String, List<WeatherMetrics>> forcastData = getForecastWeatherDataMap(list, forecastDays);

        List<Bucket> buckets = null;

        List<WeatherStatistics> weatherStatisticsResponse = new ArrayList<>();
        WeatherStatistics weatherStatistics = null;
        for (Map.Entry<String, List<WeatherMetrics>> entry : forcastData.entrySet()) {
            buckets = getBuckets(entry.getValue(), startHour, duration);
            weatherStatistics = new WeatherStatistics();
            weatherStatistics.setDate(entry.getKey());

            Metrics averageStatistics = null;
            Bucket bucket = null;
            for (int index = 0; index < buckets.size(); index++) {
                bucket = buckets.get(index);
                averageStatistics = new Metrics();
                averageStatistics.setAverageTemperature(calculateAverageTemperatures(bucket.getElements()));
                averageStatistics.setAveragePressure(calculateAveragePressure(bucket.getElements()));
                weatherStatistics.getMetrics().put(statisticGroups[index], averageStatistics);
            }

            weatherStatisticsResponse.add(weatherStatistics);
        }

        return weatherStatisticsResponse;
    }

    /**
     * get Forecast WeatherData Map
     * THis Method Create a distribute the weather data list to individual day wise list. Map key will be the date in string format.
     *
     * @param list
     * @param forecastDays
     * @return forecastData
     */
    private Map<String, List<WeatherMetrics>> getForecastWeatherDataMap(List<WeatherMetrics> list, int forecastDays) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime currentDate = customLocalDate.getCurrentDate();

        Map<String, List<WeatherMetrics>> forecastData = new LinkedHashMap<>();

        LocalDateTime futureDate = null;
        for (int i = 0; i < forecastDays; i++) {
            futureDate = currentDate.plusDays(i);
            forecastData.put(formatter.format(futureDate), new ArrayList<>());
        }

        LocalDateTime maxForcastDate = customLocalDate.getCurrentDate().plusDays(forecastDays - 1L);

        list.forEach(metric -> {
            if (metric.getStart_date().isBefore(maxForcastDate)) {
                forecastData.get(metric.getStart_date_txt()).add(metric);
            }
        });
        return forecastData;
    }

    /**
     * Calculate Average Temperatures
     * This Method calculate the average temperatures from provided list of  Weather Metrics.
     *
     * @param elements
     * @return Double
     */

    private Double calculateAverageTemperatures(List<WeatherMetrics> elements) {
        return round(elements.stream().
                mapToDouble(metric -> metric.getMain().getTemp()).
                average().
                orElse(Double.valueOf("0")), 2);
    }

    /**
     * Calculate Average Pressure
     * This Method calculate the average pressure from provided list of  Weather Metrics.
     *
     * @param elements
     * @return Double
     */

    private Double calculateAveragePressure(List<WeatherMetrics> elements) {
        return round(elements.stream().
                mapToDouble(metric -> metric.getMain().getPressure()).
                average().
                orElse(Double.valueOf("0")), 2);
    }

    /**
     * Get Buckets
     * This Method will create the buckets with provided starting hour and duration. Like if startHour is 6 and duration is 12 than it will create 2 (24/12) buckets.
     * First buckets holds that weather Metrics with starting date hour between morning 6 (startHour) to evening 18 (startHour+duration)
     * Second buckets holds that weather Metrics with starting date hour between evening(6PM) 18 to next morning 6
     *
     * @param list
     * @param startHour
     * @return duration
     */

    private List<Bucket> getBuckets(List<WeatherMetrics> list, int startHour, int duration) {

        List<Bucket> buckets = new ArrayList<>();

        initializeBuckets(buckets, startHour, duration);

        for (WeatherMetrics metrics : list) {
            for (Bucket bucket : buckets) {
                if (bucket.hasValidHour(metrics.getStart_date().getHour())) {
                    bucket.getElements().add(metrics);
                }
            }
        }
        return buckets;
    }

    /**
     * Get Initialize Buckets
     * This Method will initialize the Bucket object and ser bucket valid hours list. i.e If startHour is 6 and duration is 12 than it will create 2 (24/12) buckets.
     * First bucket valid hours list is [6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17]
     * Second bucket valid hours list is [18, 19, 20, 21, 22, 23, 0, 1, 2, 3, 4, 5]
     *
     * @param buckets
     * @param startHour
     * @param startHour
     */
    private void initializeBuckets(List<Bucket> buckets, int startHour, int duration) {

        Bucket bucket = null;
        int bucketSize = 24 / duration;

        for (int i = 0; i < bucketSize; i++) {
            bucket = new Bucket();
            bucket.setValidHours(startHour + i * duration, duration);
            buckets.add(bucket);
        }
    }
}
