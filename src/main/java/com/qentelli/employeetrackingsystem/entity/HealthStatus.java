package com.qentelli.employeetrackingsystem.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum HealthStatus {
    RED("Red"),
    AMBER("Amber"),
    GREEN("Green");
    
    private final String label;

    HealthStatus(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

}