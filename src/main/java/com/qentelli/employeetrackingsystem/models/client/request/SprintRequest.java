package com.qentelli.employeetrackingsystem.models.client.request;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SprintRequest {

	@NotBlank(message = "Sprint number is required")
	private String sprintNumber;

	@NotBlank(message = "Sprint name is required")
	private String sprintName;

	@NotNull
	@FutureOrPresent(message = "From date must be today or in the future is required")
	private LocalDate fromDate;

	@NotNull
	@FutureOrPresent(message = "To date must be today or in the future is required")
	private LocalDate toDate;

	private Boolean sprintStatus = true;

	private Boolean isEnabled = false; // default false

}
