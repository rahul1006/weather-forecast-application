package com.openWeatherMap.weatherForecast.controller;

import com.openWeatherMap.weatherForecast.domain.WeatherStatistics;
import com.openWeatherMap.weatherForecast.domain.openweathermap.WeatherForecast;
import com.openWeatherMap.weatherForecast.exception.ResourceNotFoundException;
import com.openWeatherMap.weatherForecast.util.WeatherCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.constraints.Pattern;
import java.util.List;

import static com.openWeatherMap.weatherForecast.constants.WeatherConstant.*;

@RestController
@Validated
public class WeatherController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherController.class);
    private final RestTemplate restTemplate;
    private final WeatherCalculator weatherCalculator;

    @Value("${weather.forecast.api.url}")
    private String dailyForecastApi;

    @Value("${weather.forecast.api.key}")
    private String apiKey;

    @Value("${weather.forecast.country}")
    private String country;

    @Value("${weather.forecast.startHour}")
    private Integer startHour; // (Staring hour of day to calculate whether metrics. i.e 6)

    @Value("${weather.forecast.duration}")
    private Integer duration; // (Interval on which the metrics should be calculated. i.e at every 12 hour)

    @Value("${weather.forecast.days}")
    private Integer forecastDays; // (Number of the days for which whether forecast metrics are required)

    private String[] statisticGroups = {"group[06:00 – 18:00]", "group[18:00 – 06:00]"}; // (Array of the property against which metrics value will be displayed. i.e. output json property name)

    @Autowired
    public WeatherController(RestTemplate restTemplate, WeatherCalculator weatherCalculator) {
        this.restTemplate = restTemplate;
        this.weatherCalculator = weatherCalculator;
    }

    /* Note: As per the requirement, we have only one input parameter which is city name.
     * But we have make above startHour, duration, forecastDays, statisticGroups as input params to expose forecasting metrics.
     * I  have created solution in more generic ways    *
     * Like If user need metrics at 6 hour daily 2 times and nightly 2 times for the next 5 days from today’s date than following setup is required.
     * With startHour = 6, duration = 6, forecastDays = 5, {"group[06:00 – 12:00]", "group[12:00 – 18:00]", group[18:00 – 00:00]", "group[00:00 – 18:00]"};
     *
     * With open whether map API, I have intentionally created only few domain which is required to calculate the average temperatures and pressure.
     */

    @GetMapping(value = {"/weatherMetrics", "/weatherMetrics/{city}"})
    public ResponseEntity<Object> getWeatherMetricsByCity(@PathVariable(value = CITY, required = true)
                                                          @Pattern(regexp = PATTERN_ALPHANUMERIC, message = INVALID_CITY_NAME_MSG)
                                                                  String city) {

        LOGGER.info("WeatherController, getWeatherMetrics, City = {}", city);

        try {

            StringBuilder stringBuilder = new StringBuilder();

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(dailyForecastApi)
                    .queryParam("q", stringBuilder.append(city).append(",").append(country).toString())
                    .queryParam("units", "metric")
                    .queryParam("cnt", 40)
                    .queryParam("APPID", apiKey);


            ResponseEntity<WeatherForecast> response = restTemplate.getForEntity(builder.toUriString(), WeatherForecast.class);

            WeatherForecast weatherData = response.getBody();

            if (weatherData == null || weatherData.getWeatherData() == null || weatherData.getWeatherData().isEmpty()) {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, WENT_WRONG_MESSAGE);
            }

            List<WeatherStatistics> weatherStatistics = this.weatherCalculator.getWeatherStatistics(weatherData.getWeatherData(), startHour, duration, forecastDays, statisticGroups);

            LOGGER.info("WeatherController, getWeatherMetrics, End");

            return new ResponseEntity<>(weatherStatistics, HttpStatus.OK);

        } catch (HttpClientErrorException e) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, NOT_FOUND);
        } catch (Exception e) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, WENT_WRONG_MESSAGE);
        }
    }


    /**
     * getResourceNotFound
     * This is fakll back method to handle invalid requests
     *
     * @return ResourceNotFoundException
     */
    @RequestMapping(value = "*", method = RequestMethod.GET)
    public String getResourceNotFound() {
        throw new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE);
    }
}
