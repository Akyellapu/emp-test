package com.qentelli.employeetrackingsystem.serviceImpl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.qentelli.employeetrackingsystem.entity.Project;
import com.qentelli.employeetrackingsystem.entity.WeeklySummary;
import com.qentelli.employeetrackingsystem.exception.ResourceNotFoundException;
import com.qentelli.employeetrackingsystem.models.client.request.WeeklySummaryRequest;
import com.qentelli.employeetrackingsystem.models.client.response.WeeklySummaryResponse;
import com.qentelli.employeetrackingsystem.repository.ProjectRepository;
import com.qentelli.employeetrackingsystem.repository.WeeklySummaryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WeeklySummaryService {

    private static final String WEEK = "WEEK";
    private static final String WEEKLY_SUMMARY_NOT_FOUND = "Weekly summary not found with id: ";
    private static final String NO_PROJECTS_FOUND = "No projects found for given IDs";

    private final WeeklySummaryRepository weeklySummaryRepository;
    private final ProjectRepository projectRepository;

    // 1. Save Weekly Summaries (Mon-Fri only)
    public void saveWeeklyData(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("weekFromDate/weekToDate must be provided.");
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("weekFromDate must be before or equal to weekToDate.");
        }

        LocalDate current = from.with(DayOfWeek.MONDAY);
        while (!current.isAfter(to)) {
            LocalDate weekStart = current;
            LocalDate weekEnd = weekStart.plusDays(4); // Mon to Fri

            WeeklySummary summary = new WeeklySummary();
            summary.setWeekStartDate(weekStart);
            summary.setWeekEndDate(weekEnd);
            summary.setSoftDelete(false);
            summary.setCreatedAt(LocalDateTime.now());
            summary.setWeekRange(WEEK + ": " + weekStart + " To " + weekEnd);
            summary.setUpcomingTasks(new ArrayList<>());
            summary.setListProject(new ArrayList<>());

            weeklySummaryRepository.save(summary);
            current = current.plusWeeks(1);
        }
    }

    // 2. Create Weekly Summary
    public WeeklySummaryResponse createSummary(WeeklySummaryRequest request) {
        List<Project> projects = projectRepository.findAllById(request.getProjectIds());
        if (projects.isEmpty()) {
            throw new ResourceNotFoundException(NO_PROJECTS_FOUND);
        }

        WeeklySummary summary = new WeeklySummary();
        summary.setWeekStartDate(request.getWeekStartDate());
        summary.setWeekEndDate(request.getWeekEndDate());
        summary.setUpcomingTasks(request.getUpcomingTasks());
        summary.setListProject(projects);
        summary.setSoftDelete(false);
        summary.setCreatedAt(LocalDateTime.now());

        String weekRange = WEEK + ": " + request.getWeekStartDate().format(DateTimeFormatter.ofPattern("dd-MMMM-yyyy"))
                + " To " + request.getWeekEndDate().format(DateTimeFormatter.ofPattern("dd-MMMM-yyyy"));
        summary.setWeekRange(weekRange);

        WeeklySummary saved = weeklySummaryRepository.save(summary);
        return mapToResponse(saved);
    }

    // 3. Get Summary by ID
    public WeeklySummaryResponse getSummaryById(Integer weekId) {
        WeeklySummary summary = weeklySummaryRepository.findById(weekId)
                .orElseThrow(() -> new ResourceNotFoundException(WEEKLY_SUMMARY_NOT_FOUND + weekId));
        return mapToResponse(summary);
    }

    // 4. Update Summary
    public WeeklySummaryResponse updateSummary(WeeklySummaryRequest request) {
        WeeklySummary summary = weeklySummaryRepository.findById(request.getWeekId())
                .orElseThrow(() -> new ResourceNotFoundException(WEEKLY_SUMMARY_NOT_FOUND + request.getWeekId()));

        List<Project> projects = projectRepository.findAllById(request.getProjectIds());
        if (projects.isEmpty()) {
            throw new ResourceNotFoundException(NO_PROJECTS_FOUND);
        }

        summary.setWeekStartDate(request.getWeekStartDate());
        summary.setWeekEndDate(request.getWeekEndDate());
        summary.setUpcomingTasks(request.getUpcomingTasks());
        summary.setListProject(projects);
        summary.setWeekRange(WEEK + ": " + request.getWeekStartDate().format(DateTimeFormatter.ofPattern("dd-MMMM-yyyy"))
                + " To " + request.getWeekEndDate().format(DateTimeFormatter.ofPattern("dd-MMMM-yyyy")));

        WeeklySummary updated = weeklySummaryRepository.save(summary);
        return mapToResponse(updated);
    }

    // 5. Get All Summaries (non-paginated)
    public List<WeeklySummaryResponse> getAllSummaries() {
        return weeklySummaryRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    // 6. Manual Paginated Report
    public Page<WeeklySummaryResponse> generateReport(LocalDate from, LocalDate to, Pageable pageable) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Both weekFromDate and weekToDate must be provided.");
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("weekFromDate must be before or equal to weekToDate.");
        }

        LocalDate adjustedFrom = from.with(DayOfWeek.MONDAY);
        Page<WeeklySummary> resultPage = weeklySummaryRepository
                .findByWeekStartDateBetweenAndSoftDeleteFalse(adjustedFrom, to, Pageable.unpaged());

        List<WeeklySummaryResponse> responseList = resultPage.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        return new PageImpl<>(responseList, pageable, responseList.size());
    }

    // 7. Soft Delete
    public WeeklySummary softDelete(int id) {
        WeeklySummary summary = weeklySummaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(WEEKLY_SUMMARY_NOT_FOUND + id));
        summary.setSoftDelete(true);
        return  weeklySummaryRepository.save(summary);
    }

    // 8. Hard Delete
    public void deleteSummary(Integer weekId) {
        WeeklySummary summary = weeklySummaryRepository.findById(weekId)
                .orElseThrow(() -> new ResourceNotFoundException(WEEKLY_SUMMARY_NOT_FOUND + weekId));
        weeklySummaryRepository.delete(summary);
    }

    // 9. Get Only Week Ranges (Formatted)
    public List<WeeklySummaryResponse> getFormattedWeekRanges() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy");
        return weeklySummaryRepository.findAll().stream().map(summary -> {
            WeeklySummaryResponse res = new WeeklySummaryResponse();
            res.setWeekId(summary.getWeekId());
            res.setWeekStartDate(summary.getWeekStartDate());
            res.setWeekEndDate(summary.getWeekEndDate());
            res.setWeekRange(WEEK + ": " + summary.getWeekStartDate().format(formatter) + " To " + summary.getWeekEndDate().format(formatter));
            return res;
        }).toList();
    }

    // 🔁 Helper Method for Mapping Entity → Response
    private WeeklySummaryResponse mapToResponse(WeeklySummary summary) {
        WeeklySummaryResponse response = new WeeklySummaryResponse();
        response.setWeekId(summary.getWeekId());
        response.setWeekStartDate(summary.getWeekStartDate());
        response.setWeekEndDate(summary.getWeekEndDate());
        response.setUpcomingTasks(summary.getUpcomingTasks());
        response.setProjectNames(summary.getListProject().stream().map(Project::getProjectName).toList());
        response.setWeekRange(summary.getWeekRange());
        return response;
    }
}
