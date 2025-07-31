package com.qentelli.employeetrackingsystem.exception;

public class WeeklySprintUpdateNotFoundException extends RuntimeException {

    public WeeklySprintUpdateNotFoundException(Long id) {
        super("WeeklySprintUpdate not found with ID: " + id);
    }

    public WeeklySprintUpdateNotFoundException(String message) {
        super(message);
    }
}