package com.qentelli.employeetrackingsystem.serviceImpl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.qentelli.employeetrackingsystem.entity.Sprint;
import com.qentelli.employeetrackingsystem.entity.WeekRange;
import com.qentelli.employeetrackingsystem.exception.SprintNotFoundException;
import com.qentelli.employeetrackingsystem.models.client.request.SprintRequest;
import com.qentelli.employeetrackingsystem.models.client.response.SprintResponse;
import com.qentelli.employeetrackingsystem.models.client.response.WeekRangeResponse;
import com.qentelli.employeetrackingsystem.repository.SprintRepository;

@Service
public class SprintService {

	@Autowired
	private SprintRepository sprintRepository;

	public SprintResponse createSprint(SprintRequest request) {
		Sprint sprint = new Sprint();
		sprint.setSprintNumber(request.getSprintNumber());
		sprint.setSprintName(request.getSprintName());
		sprint.setFromDate(request.getFromDate());
		sprint.setToDate(request.getToDate());

		// Generate valid weekday-only weeks
		List<WeekRange> generatedWeeks = generateWeekRanges(request.getFromDate(), request.getToDate(), sprint);
		sprint.setWeeks(generatedWeeks);

		Sprint savedSprint = sprintRepository.save(sprint);

		List<WeekRangeResponse> weekResponses = savedSprint.getWeeks().stream().filter(week -> !week.isSoftDelete())
				.map(week -> new WeekRangeResponse(week.getWeekId(), week.getWeekFromDate(), week.getWeekToDate()))
				.toList();

		return new SprintResponse(savedSprint.getSprintId(), savedSprint.getSprintNumber(), savedSprint.getSprintName(),
				savedSprint.getFromDate(), savedSprint.getToDate(), weekResponses, savedSprint.getIsEnabled(),
				savedSprint.getSprintStatus() // Assuming sprintStatus is part of the response
		);
	}

	public SprintResponse createSprint1(SprintRequest request) {
		LocalDate today = LocalDate.now();

		if (request.getFromDate().isBefore(today)) {
			throw new IllegalArgumentException("From date must be today or a future date.");
		}

		if (request.getToDate().isBefore(today)) {
			throw new IllegalArgumentException("To date must be today or a future date.");
		}

		if (request.getToDate().isBefore(request.getFromDate())) {
			throw new IllegalArgumentException("To date must be after or equal to From date.");
		}

		Sprint sprint = new Sprint();
		sprint.setSprintNumber(request.getSprintNumber());
		sprint.setSprintName(request.getSprintName());
		sprint.setFromDate(request.getFromDate());
		sprint.setToDate(request.getToDate());

		// Generate valid weekday-only weeks
		List<WeekRange> generatedWeeks = generateWeekRanges(request.getFromDate(), request.getToDate(), sprint);
		sprint.setWeeks(generatedWeeks);

		Sprint savedSprint = sprintRepository.save(sprint);

		List<WeekRangeResponse> weekResponses = savedSprint.getWeeks().stream().filter(week -> !week.isSoftDelete())
				.map(week -> new WeekRangeResponse(week.getWeekId(), week.getWeekFromDate(), week.getWeekToDate()))
				.toList();

		return new SprintResponse(savedSprint.getSprintId(), savedSprint.getSprintNumber(), savedSprint.getSprintName(),
				savedSprint.getFromDate(), savedSprint.getToDate(), weekResponses, savedSprint.getIsEnabled(),
				savedSprint.getSprintStatus() // Assuming sprintStatus is part of the response
		);
	}

	public Page<SprintResponse> getAllSprints(Pageable pageable) {
		return sprintRepository.findBySprintStatusTrue(pageable).map(this::mapToResponse);
	}

	public SprintResponse getSprintById(Long id) {
		Sprint sprint = sprintRepository.findById(id)
				.orElseThrow(() -> new SprintNotFoundException("Sprint not found with id: " + id));

		return mapToResponse(sprint);
	}

	public SprintResponse updateSprint(Long id, SprintRequest request) {
		Sprint sprint = sprintRepository.findById(id)
				.orElseThrow(() -> new SprintNotFoundException("Sprint not found with id: " + id));

		sprint.setSprintNumber(request.getSprintNumber());
		sprint.setSprintName(request.getSprintName());
		sprint.setFromDate(request.getFromDate());
		sprint.setToDate(request.getToDate());

		Sprint updated = sprintRepository.save(sprint);
		return mapToResponse(updated);
	}

	public void deleteSprint(Long id) {
		Sprint sprint = sprintRepository.findById(id)
				.orElseThrow(() -> new SprintNotFoundException("Sprint not found with id: " + id));
		sprint.setSprintStatus(false); // Assuming sprintStatus indicates active/inactive
		sprintRepository.save(sprint);
	}

	public boolean setSprintEnabled(Long sprintId) {
		Sprint sprint = sprintRepository.findById(sprintId)
				.orElseThrow(() -> new SprintNotFoundException("Sprint not found with id: " + sprintId));
		sprint.setIsEnabled(true);
		sprintRepository.save(sprint);
		return true;
	}

	private List<WeekRange> generateWeekRanges(LocalDate start, LocalDate end, Sprint sprint) {
		List<WeekRange> weekRanges = new ArrayList<>();

		// Align start date to Wednesday
		LocalDate current = start;
		if (current.getDayOfWeek() != DayOfWeek.WEDNESDAY) {
			int daysUntilWednesday = (DayOfWeek.WEDNESDAY.getValue() - current.getDayOfWeek().getValue() + 7) % 7;
			current = current.plusDays(daysUntilWednesday);
		}

		for (int i = 0; i < 3; i++) {
			LocalDate weekStart = current.plusWeeks(i);
			LocalDate weekEnd = weekStart.plusDays(6); // Tuesday

			// Ensure weekEnd does not exceed sprint end date
			if (weekEnd.isAfter(end)) {
				weekEnd = end;
			}

			WeekRange week = new WeekRange();
			week.setWeekFromDate(weekStart);
			week.setWeekToDate(weekEnd);
			week.setSoftDelete(false);
			week.setSprint(sprint);

			weekRanges.add(week);
		}

		return weekRanges;
	}

	// Helper to convert Sprint to SprintResponse
	private SprintResponse mapToResponse(Sprint sprint) {
		List<WeekRangeResponse> weekResponses = new ArrayList<>();
		if (sprint.getWeeks() != null) {
			for (WeekRange week : sprint.getWeeks()) {
				if (!week.isSoftDelete()) {
					weekResponses
							.add(new WeekRangeResponse(week.getWeekId(), week.getWeekFromDate(), week.getWeekToDate()));
				}
			}
		}

		return new SprintResponse(sprint.getSprintId(), sprint.getSprintNumber(), sprint.getSprintName(),
				sprint.getFromDate(), sprint.getToDate(), weekResponses, sprint.getSprintStatus(), // ✅ corrected
																									// position
				sprint.getIsEnabled() // ✅ corrected position
		);
	}
}