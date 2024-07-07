package com.schedulerapi.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.schedulerapi.model.User;
import com.schedulerapi.response.ErrorResponse;
import com.schedulerapi.response.SuccessResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping
    public ResponseEntity<?> getUser() {
        String[] data = { "user1", "user2" };
        
        // Return success response
        @SuppressWarnings({ "rawtypes", "unchecked" })
        SuccessResponse<String[]> successResponse = new SuccessResponse(HttpStatus.OK.value(), "Success", data);
        return ResponseEntity.ok(successResponse);
    }

    @PostMapping
    public ResponseEntity<?> addUser(@Valid @RequestBody User user, BindingResult bindingResult) {
        // Check for validation errors
        if (bindingResult.hasErrors()) {
            // Extract error messages
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());

            // Return ResponseEntity with error response
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Validation error", errors);
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // If no validation errors, proceed with business logic
        // For example, save the user to database
        // userService.save(user);

        // Return success response
        @SuppressWarnings({ "rawtypes", "unchecked" })
        SuccessResponse<String[]> successResponse = new SuccessResponse(HttpStatus.OK.value(), "Success", user);
        return ResponseEntity.ok(successResponse);
    }
}
