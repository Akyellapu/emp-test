package com.qentelli.employeetrackingsystem.serviceImpl;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.qentelli.employeetrackingsystem.entity.Sprint;
import com.qentelli.employeetrackingsystem.entity.WeekRange;
import com.qentelli.employeetrackingsystem.exception.MissingRequestDateException;
import com.qentelli.employeetrackingsystem.exception.SprintNotFoundException;
import com.qentelli.employeetrackingsystem.models.client.request.WeekRangeRequest;
import com.qentelli.employeetrackingsystem.models.client.response.WeekRangeResponse;
import com.qentelli.employeetrackingsystem.repository.SprintRepository;
import com.qentelli.employeetrackingsystem.repository.WeekRangeRepository;

@Service
public class WeekRangeService {

    @Autowired
    private WeekRangeRepository weekRangeRepository;
    
    @Autowired
    private SprintRepository sprintRepository;

    // Save weekly data in the database

    public void saveWeeklyData(WeekRangeRequest request) {
        LocalDate currentDate = request.getWeekFromDate();
        Sprint sprint = sprintRepository.findById(request.getSprintId())
            .orElseThrow(() -> new SprintNotFoundException("Sprint not found with ID: " + request.getSprintId()));

        while (!currentDate.isAfter(request.getWeekToDate())) {
            LocalDate weekStart = currentDate.with(DayOfWeek.MONDAY);
            LocalDate weekEnd = weekStart.plusDays(4);

            WeekRange weekRange = new WeekRange();
            weekRange.setWeekFromDate(weekStart);
            weekRange.setWeekToDate(weekEnd);
            weekRange.setSoftDelete(false);
            weekRange.setSprint(sprint); // ✅ Correct entity object set here

            weekRangeRepository.save(weekRange);
            currentDate = currentDate.plusWeeks(1);
        }
    }


    // Generate report excluding Saturdays and Sundays
    public Page<WeekRangeResponse> generateReport(LocalDate weekFromDate, LocalDate weekToDate, Pageable pageable) {

		if (weekFromDate == null || weekToDate == null) {
			throw new MissingRequestDateException("weekFromDate/weekToDate", "Both weekFromDate and weekToDate must be provided.");
		}

		if (weekFromDate.isAfter(weekToDate)) {
			throw new MissingRequestDateException( "weekFromDate", 
		            "weekFromDate must be before or equal to weekToDate.");
		}
    	    
    	// Adjust the weekFromDate to the start of the week (Monday)
        LocalDate adjustedWeekFromDate = weekFromDate.with(DayOfWeek.MONDAY);

        // Fetch all records without pagination
        List<WeekRange> weekRanges = weekRangeRepository.findByWeekFromDateBetweenAndSoftDeleteFalse(adjustedWeekFromDate, weekToDate, Pageable.unpaged()).getContent();
        
        // Map entities to response objects
        List<WeekRangeResponse> filteredResponses = weekRanges.stream()
                .map(weekRange -> new WeekRangeResponse(
                        weekRange.getWeekId(),
                        weekRange.getWeekFromDate(),
                        weekRange.getWeekToDate()

                ))

                .collect(Collectors.toList());

        // Return paginated response manually

        return new PageImpl<>(filteredResponses, pageable, filteredResponses.size());

    }
    
    public void softDelete(int id) {
        WeekRange range = weekRangeRepository.findById(id).orElseThrow();
        range.setSoftDelete(true); // Set soft delete flag to true
        weekRangeRepository.save(range);
    }
    
}
 