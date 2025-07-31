package com.qentelli.employeetrackingsystem.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.qentelli.employeetrackingsystem.entity.WeeklySummary;
import com.qentelli.employeetrackingsystem.exception.RequestProcessStatus;
import com.qentelli.employeetrackingsystem.models.client.request.WeeklySummaryRequest;
import com.qentelli.employeetrackingsystem.models.client.response.AuthResponse;
import com.qentelli.employeetrackingsystem.models.client.response.WeeklySummaryResponse;
import com.qentelli.employeetrackingsystem.serviceImpl.WeeklySummaryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/api/weekly-summary")
@RequiredArgsConstructor
public class WeeklySummaryController {

    private final WeeklySummaryService weeklySummaryService;

    // 1. Save weekly range (Mon–Fri only)
    @PostMapping("/generate")
    public ResponseEntity<AuthResponse<String>> generateWeeks(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        weeklySummaryService.saveWeeklyData(from, to);
        return ResponseEntity.ok(new AuthResponse<>(200, RequestProcessStatus.SUCCESS, "Weekly summaries generated."));
    }

    // 2. Create Weekly Summary
    @PostMapping
    public ResponseEntity<AuthResponse<WeeklySummaryResponse>> createSummary(@Valid @RequestBody WeeklySummaryRequest request) {
        WeeklySummaryResponse response = weeklySummaryService.createSummary(request);
    	AuthResponse<WeeklySummaryResponse> authResponse = new AuthResponse<>(
				HttpStatus.CREATED.value(),
				RequestProcessStatus.SUCCESS,
				LocalDateTime.now(),
				"Weekly summary created successfully",
				response
		);  
    	return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    // 3. Get Weekly Summary by ID
    @GetMapping("/{weekId}")
    public ResponseEntity<AuthResponse<WeeklySummaryResponse>> getSummaryById(@PathVariable Integer weekId) {
        WeeklySummaryResponse response = weeklySummaryService.getSummaryById(weekId);
        AuthResponse<WeeklySummaryResponse> authResponse = new AuthResponse<>(
				HttpStatus.OK.value(),
				RequestProcessStatus.SUCCESS,
				LocalDateTime.now(),
				"Weekly summary fetched successfully",
				response
		);
        return ResponseEntity.ok(authResponse);
    }

    // 4. Get All Weekly Summaries (non-paginated)
    @GetMapping("/all")
    public ResponseEntity<AuthResponse<List<WeeklySummaryResponse>>> getAllSummaries() {
        List<WeeklySummaryResponse> allSummaries = weeklySummaryService.getAllSummaries();
        AuthResponse<List<WeeklySummaryResponse>> authResponse = new AuthResponse<>(
				HttpStatus.OK.value(),
				RequestProcessStatus.SUCCESS,
				LocalDateTime.now(),
				"All weekly summaries fetched successfully",
				allSummaries
		);
		return ResponseEntity.ok(authResponse);
    }

    // 5. Update Weekly Summary
    @PutMapping
    public ResponseEntity<AuthResponse<WeeklySummaryResponse>> updateSummary(@Valid @RequestBody WeeklySummaryRequest request) {
        WeeklySummaryResponse updated = weeklySummaryService.updateSummary(request);
        AuthResponse<WeeklySummaryResponse> authResponse = new AuthResponse<>(
				HttpStatus.OK.value(),
				RequestProcessStatus.SUCCESS,
				LocalDateTime.now(),
				"Weekly summary updated successfully",
				updated
		);
		return ResponseEntity.ok(authResponse);
    }

    // 6. Soft Delete
    @PatchMapping("/soft-delete/{weekId}")
    public ResponseEntity<AuthResponse<WeeklySummaryResponse>> softDelete(@PathVariable Integer weekId) {
    	WeeklySummary softDeleted = weeklySummaryService.softDelete(weekId);
        WeeklySummaryResponse response = new WeeklySummaryResponse();
		response.setWeekId(softDeleted.getWeekId());
		response.setWeekStartDate(softDeleted.getWeekStartDate());
		response.setWeekEndDate(softDeleted.getWeekEndDate());
		response.setUpcomingTasks(softDeleted.getUpcomingTasks());

		AuthResponse<WeeklySummaryResponse> authResponse = new AuthResponse<>(
				HttpStatus.OK.value(),
				RequestProcessStatus.SUCCESS,
				LocalDateTime.now(),
				"Weekly summary soft deleted successfully",
				response
		);
		return ResponseEntity.ok(authResponse);
        
    }

    // 7. Hard Delete
    @DeleteMapping("/{weekId}")
    public ResponseEntity<AuthResponse<String>> hardDelete(@PathVariable Integer weekId) {
        weeklySummaryService.deleteSummary(weekId);
        return ResponseEntity.ok(new AuthResponse<>(200, RequestProcessStatus.SUCCESS, "Permanently deleted weekId: " + weekId));
    }

  /*
    // 8. Get Only Formatted Week Ranges
    @GetMapping("/ranges")
    public ResponseEntity<AuthResponse<List<WeeklySummaryResponse>>> getWeekRanges() {
        List<WeeklySummaryResponse> weekRanges = weeklySummaryService.getFormattedWeekRanges();
        
    }

*/
    
    // 9. Generate Paginated Report by Date Range
    @GetMapping("/by-range")
    public ResponseEntity<AuthResponse<List<WeeklySummaryResponse>>> getByDateRange(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<WeeklySummaryResponse> pageData = weeklySummaryService.generateReport(from, to, pageable);

        AuthResponse<List<WeeklySummaryResponse>> authResponse = new AuthResponse<>(
				HttpStatus.OK.value(),
				RequestProcessStatus.SUCCESS,
				LocalDateTime.now(),
				"All weekly summaries fetched successfully",
				pageData.getContent()
		);
		return ResponseEntity.ok(authResponse);
    }
}
