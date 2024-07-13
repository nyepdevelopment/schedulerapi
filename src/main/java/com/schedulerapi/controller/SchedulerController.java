package com.schedulerapi.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.schedulerapi.constants.GlobalConstants;
import com.schedulerapi.response.SuccessResponse;
import com.schedulerapi.store.LogsDataStore;

@RestController
@RequestMapping("/api/scheduler")
public class SchedulerController {
    @Autowired
    LogsDataStore logsDataStore = new LogsDataStore();

    @GetMapping
    public ResponseEntity<?> getScheduler() {
        List<String> currentScheduledList = Optional.ofNullable(logsDataStore.getData("logs"))
                .orElse(Collections.emptyList());

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");

        // Assuming you are using a cron library that has CronExpression class
        var expression = CronExpression.parse(GlobalConstants.API_CRON_EXPRESSION);
        LocalDateTime localDateTime = LocalDateTime.now();

        // Get the next schedule
        LocalDateTime nextSchedule = expression.next(localDateTime);

        // Format the next schedule time with the formatter
        @SuppressWarnings("null")
        String formattedDateTimeNext = nextSchedule.format(dateTimeFormatter);

        // Set data as an object of currentScheduledListCount and nextScheduleDateTime
        Map<String, Object> data = new HashMap<>();
        data.put("currentScheduledListCount", currentScheduledList.size());
        data.put("nextScheduleDateTime", formattedDateTimeNext);

        // Return success response
        @SuppressWarnings({ "rawtypes", "unchecked" })
        SuccessResponse<String[]> successResponse = new SuccessResponse(HttpStatus.OK.value(), "Success", data);
        return ResponseEntity.ok(successResponse);
    }
}
