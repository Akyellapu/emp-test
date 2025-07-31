package com.qentelli.employeetrackingsystem.models.client.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeekRangeResponse {
	private int weekId;
	private LocalDate weekFromDate;
	private LocalDate weekToDate;
}