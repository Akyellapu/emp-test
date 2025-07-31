package com.qentelli.employeetrackingsystem.models.client.response;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SprintResponse {
	private Long sprintId;
	private String sprintNumber;
	private String sprintName;
	private LocalDate fromDate;
	private LocalDate toDate;
	private List<WeekRangeResponse> weeks;
	private Boolean sprintStatus;

	private Boolean isEnabled;

}
