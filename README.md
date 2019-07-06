# Getting Started with Weather forecast Service

## Reference Documentation
* This is Weather forecast API which forecast Average Temperature and Pressure for provided number of days.(like 3 days)
* THIS API retrieve weather metrics of a specific city of India only.
* This API is using [openweathermap](https://openweathermap.org/) to retrieve the data for
this case study.

## Start Application

* Execute following command from project **output** directory.
  * java -jar weatherForecast-0.0.1-SNAPSHOT.jar
  * [http://localhost:8080/weatherMetrics/Ahmedabad](http://localhost:8080/weatherMetrics/Ahmedabad) (City should be from India only)
  * Temperature is in Celsius and Pressure is in hPa
## Endpoints
* THis API has single end point which accept CITY parameter containing the city’s name as the input for
the correct response.
* The endpoint is returning a JSON object that gives the averages of the temperature and 
pressure:
* **Temperature is in Celsius and Pressure is atmospheric pressure on the sea level in hPa**
* For example, Average of daily (06:00 – 18:00) and nightly (18:00 – 06:00) temperatures and pressure for the next 3 days from today’s date shown as below.
* [http://localhost:8080/weatherMetrics/Ahmedabad](http://localhost:8080/weatherMetrics/Ahmedabad)
    ```
    [
        {
            "date": "2019-07-06",
            "metrics": {
                "group[06:00 – 18:00]": {
                    "averageTemperature": 32.96,
                    "averagePressure": 997.42
                },
                "group[18:00 – 06:00]": {
                    "averageTemperature": 29.52,
                    "averagePressure": 998.69
                }
            }
        },
        {
            "date": "2019-07-07",
            "metrics": {
                "group[06:00 – 18:00]": {
                    "averageTemperature": 32.8,
                    "averagePressure": 999.24
                },
                "group[18:00 – 06:00]": {
                    "averageTemperature": 29,
                    "averagePressure": 999.82
                }
            }
        },
        {
            "date": "2019-07-08",
            "metrics": {
                "group[06:00 – 18:00]": {
                    "averageTemperature": 34.04,
                    "averagePressure": 999.63
                },
                "group[18:00 – 06:00]": {
                    "averageTemperature": 29.06,
                    "averagePressure": 1001.14
                }
            }
        }
    ]
    ```


## Build Application

**Prerequisite**
* Java 1.8 and Maven should be installed
* JAVA_HOME and MAVEN_HOME environment should there.
* There should not be proxy to block [https://api.openweathermap.org/data/2.5/forecast](https://api.openweathermap.org/data/2.5/forecast) API.

**Command**
* Execute following command from project root directory.
  * mvn clean install   
  
  This will create the executable JAR (weatherForecast-0.0.1-SNAPSHOT.jar) under the target directory. 
  
## Run Application

* Execute following command from project target directory (where executable jar is located) directory.
  * java -jar weatherForecast-0.0.1-SNAPSHOT.jar
* Use following API to get Weather Metrics data of any city of India.
  * [http://localhost:8080/weatherMetrics/{cityName}](http://localhost:8080/weatherMetrics/Ahmedabad)
  
## Solution Approach

* This application is build using Spring Boot 2.1.3 and Java 1.8.
* Application have proper error handling mechanism. This API will return the appropriate error codes when invalid input received or data not found for given city.
  * With Invalid City Name [http://localhost:8080/weatherMetrics/Ahmedabad123](http://localhost:8080/weatherMetrics/Ahmedabad123)
      ```
      {
        "code": "400",
        "message": "400 city not found"
      }
      ```
   * WithoutCity Name [http://localhost:8080/weatherMetrics]()
      ```
      {
        "code": "400",
        "message": "city name is mandatory"
      }
      ```
    * Invalid URL [http://localhost:8080]() or [http://localhost:8080/asdf]()
      ```
      {
        "code": "400",
        "message": "Invalid URL"
      }
      ```
* As per the requirement, we have only one input parameter which is city name.But I have created the generic solution by making startHour, duration, forecastDays, statisticGroups as input params to expose forecasting metrics.
Like If user need metrics at every 6 hour, mean daily 2 times and nightly 2 times for the next 5 days from today’s date than following setup is required.
    * startHour = 6, 
    * duration = 6, 
    * forecastDays = 5, 
    * statisticGroups = {"group[06:00 – 12:00]", "group[12:00 – 18:00]", group[18:00 – 00:00]", "group[00:00 – 06:00]"}
    
    ```
    [
        {
            "date": "2019-07-06",
            "metrics": {
                "group[06:00 – 12:00]": {
                    "averageTemperature": 32.96,
                    "averagePressure": 997.42
                },
                "group[12:00 – 18:00]": {
                    "averageTemperature": 29.52,
                    "averagePressure": 998.69
                },
                "group[18:00 – 00:00]": {
                    "averageTemperature": 32.96,
                    "averagePressure": 997.42
                },
                "group[00:00 – 06:00]": {
                    "averageTemperature": 29.52,
                    "averagePressure": 998.69
                }
            }
        }
    ]
    ```