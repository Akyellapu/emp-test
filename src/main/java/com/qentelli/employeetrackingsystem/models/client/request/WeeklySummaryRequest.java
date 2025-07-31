package com.qentelli.employeetrackingsystem.models.client.request;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
public class WeeklySummaryRequest {
	
    private int weekId;
    
    @NotNull(message = "Week start date is required")
    private LocalDate weekStartDate;

    @NotNull(message = "Week end date is required")
    private LocalDate weekEndDate;

    @NotEmpty(message = "Upcoming tasks must not be empty")
    @Size(min = 1, message = "At least one upcoming task is required")
    private List<String> upcomingTasks;

    private boolean status; // assuming this is optional and defaults to false

    @NotEmpty(message = "Project IDs must not be empty")
    @Size(min = 1, message = "At least one project ID is required")
    private List<Integer> projectIds;
}