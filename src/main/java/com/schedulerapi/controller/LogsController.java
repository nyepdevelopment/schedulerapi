package com.schedulerapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.schedulerapi.response.SuccessResponse;
import com.schedulerapi.store.RuntimeDataStore;

@RestController
@RequestMapping("/api/logs")
public class LogsController {
    @Autowired
    RuntimeDataStore runtimeDataStore = new RuntimeDataStore();

    @GetMapping
    public ResponseEntity<?> getUser() {
        List<String> currentScheduledList = runtimeDataStore.getData("scheduled");
        
        // Return success response
        @SuppressWarnings({ "rawtypes", "unchecked" })
        SuccessResponse<String[]> successResponse = new SuccessResponse(HttpStatus.OK.value(), "Success", currentScheduledList);
        return ResponseEntity.ok(successResponse);
    }
}
