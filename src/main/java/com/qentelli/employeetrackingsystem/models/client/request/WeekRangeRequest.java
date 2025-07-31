package com.qentelli.employeetrackingsystem.models.client.request;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class WeekRangeRequest {

	private int weekId;
	private LocalDate weekFromDate;
	private LocalDate weekToDate;
	private boolean isActive=true;
	private boolean softDelete=false;

	// You can either expose Sprint ID or a nested SprintDto
	private Long sprintId;

}