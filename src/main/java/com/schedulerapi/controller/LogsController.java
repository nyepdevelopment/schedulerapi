package com.schedulerapi.controller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.schedulerapi.response.SuccessResponse;
import com.schedulerapi.store.LogsDataStore;

@RestController
@RequestMapping("/api/logs")
public class LogsController {
    @Autowired
    LogsDataStore logsDataStore = new LogsDataStore();

    @GetMapping
    public ResponseEntity<?> getLogs() {
        List<String> currentScheduledList = Optional.ofNullable(logsDataStore.getData("logs"))
                .orElse(Collections.emptyList());

        // Return success response
        @SuppressWarnings({ "rawtypes", "unchecked" })
        SuccessResponse<String[]> successResponse = new SuccessResponse(HttpStatus.OK.value(), "Success",
                currentScheduledList);
        return ResponseEntity.ok(successResponse);
    }
}
