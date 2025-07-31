package com.qentelli.employeetrackingsystem.models.client.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.qentelli.employeetrackingsystem.entity.HealthStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeeklySprintUpdateDto {
	private Integer weekSprintId;

	private String sprintNumber;
	// Using IDs to keep DTO lightweight
	private int weeekRangeId; // consider renaming to weekRangeId
	private int projectId;

	private String projectName; // ✅ Added field to hold project name
	private int assignedPoints;
	private int assignedStoriesCount;
	private int inDevPoints;
	private int inDevStoriesCount;
	private int inQaPoints;
	private int inQaStoriesCount;
	private int completePoints;
	private int completeStoriesCount;
	private int blockedPoints;
	private int blockedStoriesCount;

	private double completePercentage;

	private String estimationHealth;
	private String groomingHealth;
	@JsonProperty("estimationHealthStatus")
	private HealthStatus estimationHealthStatus;

	@JsonProperty("groomingHealthStatus")
	private HealthStatus groomingHealthStatus;

	private int difficultCount1;
	private int difficultCount2;
	private int riskPoints;
	private int riskStoryCounts;

	private String comments; // backend-side comment from frontend
	private Integer injectionPercentage;
	private boolean weeklySprintUpdateStatus = true; // true means active, false means inactive;

	private boolean isEnabled = false; // default false, indicates not enabled

	
}
