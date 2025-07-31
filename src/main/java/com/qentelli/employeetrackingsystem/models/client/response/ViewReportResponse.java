package com.qentelli.employeetrackingsystem.models.client.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.qentelli.employeetrackingsystem.entity.TaskStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViewReportResponse {
	private int viewReportId;
	private WeekRangeResponse weekRange;
	private String taskName;
	private TaskStatus taskStatus;
	private List<String> summary;
	private List<String> keyAccomplishment;
	private List<String> comments;
	private List<String> upcomingTasks;
	private String projectName;
	private String personName;
	private LocalDate taskStartDate;
	private LocalDate taskEndDate;
	private LocalDateTime createdAt;
	private String createdBy;
	
	public ViewReportResponse(int viewReportId, 
			 String projectName,
			 String personName,
			 List<String> upcomingTasks,
			 List<String> summary,
			 TaskStatus taskStatus,
			 List<String> keyAccomplishment) {
		this.viewReportId = viewReportId;
		this.taskStatus = taskStatus;
		this.summary = summary;
		this.keyAccomplishment = keyAccomplishment;
		this.upcomingTasks = upcomingTasks;
		this.projectName = projectName;
		this.personName = personName;
	}
}