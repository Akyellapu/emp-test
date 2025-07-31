package com.qentelli.employeetrackingsystem.controller;

import com.qentelli.employeetrackingsystem.exception.RequestProcessStatus;
import com.qentelli.employeetrackingsystem.models.client.request.WeekRangeRequest;
import com.qentelli.employeetrackingsystem.models.client.response.AuthResponse;
import com.qentelli.employeetrackingsystem.models.client.response.WeekRangeResponse;
import com.qentelli.employeetrackingsystem.serviceImpl.WeekRangeService;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/week-ranges")
public class WeekRangeController {

	private static final Logger logger = LoggerFactory.getLogger(WeekRangeController.class);

	@Autowired
	private WeekRangeService service;

	/**
	 * POST API to save weekly data (Monday to Friday only).
	 */
	@PostMapping("/save")
	public ResponseEntity<AuthResponse<String>> saveWeeklyData(@Valid @RequestBody WeekRangeRequest request) {
		logger.info("Saving weekly data from {} to {}", request.getWeekFromDate(), request.getWeekToDate());
		service.saveWeeklyData(request);
		logger.info("Weekly data saved successfully");

		AuthResponse<String> response = new AuthResponse<>(HttpStatus.CREATED.value(), RequestProcessStatus.SUCCESS,
				"Weekly data saved successfully.");

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	/**
	 * GET API to fetch weekly report data based on a date range with pagination.
	 */
	@GetMapping("/report")
	public ResponseEntity<AuthResponse<Map<String, Object>>> generateReport(@RequestParam LocalDate weekFromDate,
			@RequestParam LocalDate weekToDate, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		logger.info("Generating weekly report from {} to {} with page {} and size {}", weekFromDate, weekToDate, page,
				size);

		Pageable pageable = Pageable.ofSize(size).withPage(page);
		Page<WeekRangeResponse> report = service.generateReport(weekFromDate, weekToDate, pageable);

		Map<String, Object> data = new HashMap<>();
		data.put("content", report.getContent());
		data.put("pageable", Map.of("pageNumber", report.getNumber(), "pageSize", report.getSize()));
		data.put("totalElements", report.getTotalElements());
		data.put("totalPages", report.getTotalPages());

		logger.info("Weekly report generated successfully with {} records", report.getNumberOfElements());

		AuthResponse<Map<String, Object>> response = new AuthResponse<>(HttpStatus.OK.value(),
				RequestProcessStatus.SUCCESS, LocalDateTime.now(), "Weekly report fetched successfully", data);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * DELETE API to perform soft delete for a week range.
	 */
	@DeleteMapping("/inActive/{id}")
	public ResponseEntity<AuthResponse<Void>> softDelete(@PathVariable int id) {
		logger.info("inActive week range with ID {}", id);
		service.softDelete(id);
		logger.info("Week range inActive successfully");

		AuthResponse<Void> response = new AuthResponse<>(HttpStatus.OK.value(), RequestProcessStatus.SUCCESS,
				LocalDateTime.now(), "Week range inActive  successfully", null);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
