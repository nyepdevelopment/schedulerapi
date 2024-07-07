package com.schedulerapi.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.schedulerapi.store.RuntimeDataStore;

@Service
public class SchedulerService {
    RestTemplate nyepApiRestTemplate = new RestTemplate();
    RestTemplate thisApiRestTemplate = new RestTemplate();

    @Autowired
    RuntimeDataStore runtimeDataStore = new RuntimeDataStore();

    List<String> newScheduledList = new ArrayList<>();
         
    List<String> currentScheduledList = runtimeDataStore.getData("scheduled");

    @Scheduled(cron = "0 */5 * * * *") // Cron expression for running every 5 minutes
    public void execute() {
        String nyepApiUrl = "https://nyep-api.onrender.com/api/portfolio/website"; // NYEP API
        String thisApiUrl = "https://schedulerapi.onrender.com/api/users"; // This service API

        HttpHeaders headers = new HttpHeaders();

        headers.set("Geolocation",
                "{\"ip\":\"103.196.139.113\",\"city\":\"Santa Rosa\",\"country\":\"PH\",\"locationCoordinates\":{\"latitude\":14.3141,\"longitude\":121.1121}}");

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // Set error handlers
        nyepApiRestTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                // do nothing
            }

            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return false;
            }
        });

        thisApiRestTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                // do nothing
            }

            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return false;
            }
        });

        // HTTP requests to APIs
        ResponseEntity<String> nyepApiResponseEntity = nyepApiRestTemplate.exchange(nyepApiUrl, HttpMethod.GET, requestEntity,
                String.class);

        ResponseEntity<String> thisApiResponseEntity = thisApiRestTemplate.exchange(thisApiUrl, HttpMethod.GET, requestEntity,
        String.class);

        String nyepApiStatusCode = nyepApiResponseEntity.getStatusCode().toString();
        String thisApiStatusCode = thisApiResponseEntity.getStatusCode().toString();

        if (currentScheduledList != null) {
            newScheduledList.addAll(currentScheduledList);
        }

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.now();
        String formattedDateTime = localDateTime.format(dateTimeFormatter);
        
        newScheduledList.add("NYEP API: " + nyepApiStatusCode + " " + "This API: " + thisApiStatusCode + " " + "Date: " + formattedDateTime);

        runtimeDataStore.setData("scheduled", newScheduledList);
    }
}