package com.qentelli.employeetrackingsystem.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.qentelli.employeetrackingsystem.entity.ViewReports;
import com.qentelli.employeetrackingsystem.exception.RequestProcessStatus;
import com.qentelli.employeetrackingsystem.models.client.request.ViewReportRequest;
import com.qentelli.employeetrackingsystem.models.client.response.AuthResponse;
import com.qentelli.employeetrackingsystem.models.client.response.PaginatedResponse;
import com.qentelli.employeetrackingsystem.models.client.response.ViewReportResponse;
import com.qentelli.employeetrackingsystem.serviceImpl.ViewReportService;

@RestController
@RequestMapping("/api/view-report")
public class ViewReportController {

    private static final Logger logger = LoggerFactory.getLogger(ViewReportController.class);

    @Autowired
    private ViewReportService viewReportService;

    @PostMapping("/createStatus")
    public ResponseEntity<AuthResponse<ViewReportResponse>> createReport(@RequestBody ViewReportRequest request) {
        logger.info("Creating report for personId: {}", request.getPersonId());
        ViewReportResponse response = viewReportService.saveReport(request);
        logger.info("Report created with ID: {}", response.getViewReportId());

        AuthResponse<ViewReportResponse> authResponse = new AuthResponse<>(
                HttpStatus.CREATED.value(),
                RequestProcessStatus.SUCCESS,
                "Report created successfully"
        );
       // authResponse.setData(response);
        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }
    
    @GetMapping("/task-view")
    public ResponseEntity<AuthResponse<PaginatedResponse<ViewReportResponse>>> getTaskTableView(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        logger.info("Received request to fetch task view from {} to {}, page: {}, size: {}", fromDate, toDate, page, size);

        PaginatedResponse<ViewReportResponse> paginatedTasks = viewReportService.getTasksByWeek(fromDate, toDate, page, size);

        logger.debug("Fetched {} tasks for the given date range", paginatedTasks.getContent().size());

        return ResponseEntity.ok(new AuthResponse<>(
            200,
            RequestProcessStatus.SUCCESS,
            LocalDateTime.now(),
            "Task table view fetched successfully",
            paginatedTasks
        ));
    }

    @GetMapping("/AllReports")
    public ResponseEntity<AuthResponse<PaginatedResponse<ViewReportResponse>>> getAllReportsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        logger.info("Fetching paginated reports - Page: {}, Size: {}", page, size);

        Page<ViewReportResponse> pagedResult = viewReportService.getAllReportsPaginated(PageRequest.of(page, size));

        PaginatedResponse<ViewReportResponse> paginatedResponse = new PaginatedResponse<>(
                pagedResult.getContent(),
                pagedResult.getNumber(),
                pagedResult.getSize(),
                pagedResult.getTotalElements(),
                pagedResult.getTotalPages(),
                pagedResult.isLast()
        );

        AuthResponse<PaginatedResponse<ViewReportResponse>> authResponse = new AuthResponse<>(
                HttpStatus.OK.value(),
                RequestProcessStatus.SUCCESS,
                LocalDateTime.now(),
                "Paginated reports fetched successfully",
                paginatedResponse
        );

        return ResponseEntity.ok(authResponse);
    }

    @GetMapping("/all")
    public ResponseEntity<AuthResponse<Map<String, Object>>> getAllReports() {
        logger.info("Fetching all reports without pagination");

        List<ViewReportResponse> reports = viewReportService.getAllReports(); // Fetch all reports

        // Wrap the list inside a map with "content" key
        Map<String, Object> data = new HashMap<>();
        data.put("content", reports);

        // Prepare the AuthResponse with status, message, and data
        AuthResponse<Map<String, Object>> authResponse = new AuthResponse<>(
                HttpStatus.OK.value(),
                RequestProcessStatus.SUCCESS,
                LocalDateTime.now(),
                "All reports fetched successfully",
                data
        );

        return ResponseEntity.ok(authResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthResponse<ViewReportResponse>> getReportById(@PathVariable Integer id) {
        logger.info("Fetching report by ID: {}", id);
        ViewReportResponse response = viewReportService.getReportById(id);
        logger.info("Report found: {}", response);

        AuthResponse<ViewReportResponse> authResponse = new AuthResponse<>(
                HttpStatus.OK.value(),
                RequestProcessStatus.SUCCESS,
                LocalDateTime.now(),
                "Report fetched successfully",
                response
        );
        return ResponseEntity.ok(authResponse);
    }

    @PutMapping("/update")
    public ResponseEntity<AuthResponse<ViewReportResponse>> updateReport(@RequestBody ViewReportRequest request) {
        logger.info("Updating report with ID: {}", request.getViewReportId());
        ViewReportResponse response = viewReportService.updateReport(request);
        logger.info("Report updated successfully: {}", response.getViewReportId());

        AuthResponse<ViewReportResponse> authResponse = new AuthResponse<>(
                HttpStatus.OK.value(),
                RequestProcessStatus.SUCCESS,
                "Report updated successfully"
        );
     //   authResponse.setData(response);
        return ResponseEntity.ok(authResponse);
    }

    // SOFT DELETE
    @DeleteMapping("/softDeleteViewReport/{id}")
    public ResponseEntity<AuthResponse<ViewReportResponse>> softDeleteViewReport(@PathVariable int id) {
        logger.info("Soft deleting view report with ID: {}", id);
        viewReportService.softDeleteSummery(id);
        logger.info("Soft delete completed for ID: {}", id);

        AuthResponse<ViewReportResponse> response = new AuthResponse<>(
                HttpStatus.OK.value(),
                RequestProcessStatus.SUCCESS,
                "ViewReport soft deleted successfully"
        );

        return ResponseEntity.ok(response);
    }

    // HARD DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<AuthResponse<Void>> deleteReport(@PathVariable Integer id) {
        logger.info("Hard deleting report with ID: {}", id);
        viewReportService.deleteReport(id);
        logger.info("Hard delete successful for ID: {}", id);

        AuthResponse<Void> authResponse = new AuthResponse<>(
                HttpStatus.NO_CONTENT.value(),
                RequestProcessStatus.SUCCESS,
                "Report deleted successfully"
        );
        return new ResponseEntity<>(authResponse, HttpStatus.NO_CONTENT);
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<ViewReports>> searchReports(
            @RequestParam(required = false) Integer personId,
            @RequestParam(required = false) Integer projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ViewReports> result = viewReportService.searchViewReports(personId, projectId, page, size);
        return ResponseEntity.ok(result);
    }

}