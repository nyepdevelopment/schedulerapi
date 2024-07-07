package com.schedulerapi.response;

import java.util.List;

public class ErrorResponse {
    private int code;
    private String message;
    private List<String> errors;

    public ErrorResponse(int code, String message, List<String> errors) {
        this.code = code;
        this.message = message;
        this.errors = errors;
    }

    // Getters and setters
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getDetails() {
        return errors;
    }

    public void setDetails(List<String> errors) {
        this.errors = errors;
    }
}
