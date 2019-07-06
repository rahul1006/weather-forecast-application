package com.openWeatherMap.weatherForecast;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openWeatherMap.weatherForecast.controller.WeatherController;
import com.openWeatherMap.weatherForecast.domain.ExceptionResponse;
import com.openWeatherMap.weatherForecast.domain.openweathermap.WeatherForecast;
import com.openWeatherMap.weatherForecast.util.CustomLocalDate;
import com.openWeatherMap.weatherForecast.util.WeatherCalculator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@WebMvcTest(WeatherController.class)
public class WeatherControllerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherController.class);

    @MockBean
    RestTemplate restTemplate;
    @Mock
    WeatherCalculator weatherCalculator;
    ObjectMapper mapper = null;
    @MockBean
    private CustomLocalDate customLocalDate;
    @Autowired
    private MockMvc mockMvc;

    @Before
    public void setup() {
        mapper = new ObjectMapper();
    }

    @Test
    public void shouldReturnMockedWeatherStatistics_cityIsValid() throws Exception {

        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.<Class<WeatherForecast>>any())).thenReturn(getMockWeatherAPIResponse());


        Clock clock = Clock.fixed(Instant.parse("2019-07-05T09:00:00.00Z"), ZoneId.of("UTC"));
        LocalDateTime dateTime = LocalDateTime.now(clock);
        Mockito.when(customLocalDate.getCurrentDate()).thenReturn(dateTime);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get("/weatherMetrics/Ahmedabad"))
                .andReturn().getResponse();
        assertThat(response.getStatus(), is(HttpStatus.OK.value()));
        JSONAssert.assertEquals(response.getContentAsString(), getMockWeatherStatistics(), false);
    }

    @Test
    public void shouldThrowError_whenCityIsNotValid() throws Exception {

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get("/weatherMetrics"))
                .andReturn().getResponse();

        ExceptionResponse exceptionResponse = mapper.readValue(response.getContentAsString(), ExceptionResponse.class);

        assertThat(response.getStatus(), is(HttpStatus.BAD_REQUEST.value()));
        assertThat(exceptionResponse.getMessage(), is("city name is mandatory"));
    }

    @Test
    public void shouldThrowError_whenInvalidURL() throws Exception {

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get("/asdas"))
                .andReturn().getResponse();

        ExceptionResponse exceptionResponse = mapper.readValue(response.getContentAsString(), ExceptionResponse.class);

        assertThat(response.getStatus(), is(HttpStatus.BAD_REQUEST.value()));
        assertThat(exceptionResponse.getMessage(), is("Invalid URL"));
    }

    @Test
    public void shouldThrowError_whenInvaidRequestMethod() throws Exception {

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post("/asdas"))
                .andReturn().getResponse();

        ExceptionResponse exceptionResponse = mapper.readValue(response.getContentAsString(), ExceptionResponse.class);

        assertThat(response.getStatus(), is(HttpStatus.BAD_REQUEST.value()));
        assertThat(exceptionResponse.getMessage(), is("Request method 'POST' not supported"));
    }

    private ResponseEntity<WeatherForecast> getMockWeatherAPIResponse() {
        JsonNode jsonNode = readJsonFile("mock-open-weather-map-api-response.json");
        WeatherForecast weatherData = null;
        try {
            weatherData = mapper.readValue(mapper.writeValueAsString(jsonNode), WeatherForecast.class);
        } catch (Exception e) {
            LOGGER.error("Error occurred in getMockWeatherAPIResponse message = {} ", e.getMessage(), e);
        }
        return new ResponseEntity<>(weatherData, HttpStatus.OK);
    }

    private String getMockWeatherStatistics() {
        JsonNode jsonNode = readJsonFile("mock-weather-statistics-response.json");
        String response = null;
        try {
            response = mapper.writeValueAsString(jsonNode);

        } catch (Exception e) {
            LOGGER.error("Error occurred in getMockWeatherStatistics message = {} ", e.getMessage(), e);
        }
        return response;
    }

    private JsonNode readJsonFile(String file) {
        JsonNode jsonNode = null;
        try (InputStream inputStream = WeatherControllerTest.class.getClassLoader().getResourceAsStream("json-response/" + file)) {
            jsonNode = mapper.readTree(inputStream);
        } catch (Exception e) {
            LOGGER.error("Error occurred while reading JSON  file = {}", file);
        }
        return jsonNode;
    }
}
