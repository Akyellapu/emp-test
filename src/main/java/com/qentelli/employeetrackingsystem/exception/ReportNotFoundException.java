package com.qentelli.employeetrackingsystem.exception;
public class ReportNotFoundException extends RuntimeException {
    public ReportNotFoundException(String message) {
        super(message);
    }
}