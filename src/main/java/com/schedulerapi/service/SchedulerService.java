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

    @Scheduled(cron = "0 */12 * * * *") // Cron expression for running every 12 minutes
    public void callApis() {
        String nyepApiUrl = "https://nyep-api.onrender.com/api/portfolio/website"; // NYEP API
        String thisApiUrl = "https://schedulerapi.onrender.com/api/logs"; // This service API

        // Set date now
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");
        LocalDateTime localDateTime = LocalDateTime.now();

        // Format the date-time now with the formatter
        String formattedDateTimeNow = localDateTime.format(dateTimeFormatter);

        // Set headers (necessary for NYEP API)
        HttpHeaders headers = new HttpHeaders();

        headers.set("Geolocation",
                "{\"ip\":\"103.196.139.113\",\"city\":\"Santa Rosa\",\"country\":\"PH\",\"locationCoordinates\":{\"latitude\":14.3141,\"longitude\":121.1121}}");

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // Set error handlers
        nyepApiRestTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public void handleError(@SuppressWarnings("null") ClientHttpResponse response) throws IOException {
                // do nothing
            }

            @Override
            public boolean hasError(@SuppressWarnings("null") ClientHttpResponse response) throws IOException {
                return false;
            }
        });

        thisApiRestTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public void handleError(@SuppressWarnings("null") ClientHttpResponse response) throws IOException {
                // do nothing
            }

            @Override
            public boolean hasError(@SuppressWarnings("null") ClientHttpResponse response) throws IOException {
                return false;
            }
        });

        // HTTP GET requests to APIs
        ResponseEntity<String> nyepApiResponseEntity = nyepApiRestTemplate.exchange(nyepApiUrl, HttpMethod.GET,
                requestEntity,
                String.class);

        ResponseEntity<String> thisApiResponseEntity = thisApiRestTemplate.exchange(thisApiUrl, HttpMethod.GET,
                requestEntity,
                String.class);

        String nyepApiStatusCode = nyepApiResponseEntity.getStatusCode().toString();
        String thisApiStatusCode = thisApiResponseEntity.getStatusCode().toString();

        // If there are already schedules in the current schedule list, add all to new
        // schedule list
        if (currentScheduledList != null) {
            newScheduledList.addAll(currentScheduledList);
        }

        // Add response to "schedule" runtime data store
        newScheduledList.add("NYEP API: " + nyepApiStatusCode + " " + "This API: " + thisApiStatusCode + " " + "Date: "
                + formattedDateTimeNow);

        // Remove not todays schedules
        newScheduledList.removeIf(schedule -> {
            // Get date from schedule string
            String[] scheduleParts = schedule.split("Date: ");
            String dateTimePart = scheduleParts[1].trim();
            String datePart = dateTimePart.split(" ")[0];

            DateTimeFormatter dateOnlyformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            // Get date two days before today
            String todayFormatted = localDateTime.minusDays(2).format(dateOnlyformatter);
            
            // Compare datePart with two days before formatted date
            boolean isTwoDaysBefore = datePart.equals(todayFormatted);
            
            return isTwoDaysBefore;
        });

        runtimeDataStore.setData("scheduled", newScheduledList);
    }
}