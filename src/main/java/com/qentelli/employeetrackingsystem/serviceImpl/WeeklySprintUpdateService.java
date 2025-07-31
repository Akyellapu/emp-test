package com.qentelli.employeetrackingsystem.serviceImpl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.qentelli.employeetrackingsystem.entity.Project;
import com.qentelli.employeetrackingsystem.entity.WeekRange;
import com.qentelli.employeetrackingsystem.entity.WeeklySprintUpdate;
import com.qentelli.employeetrackingsystem.exception.ProjectNotFoundException;
import com.qentelli.employeetrackingsystem.exception.WeekRangeNotFoundException;
import com.qentelli.employeetrackingsystem.exception.WeeklySprintUpdateNotFoundException;
import com.qentelli.employeetrackingsystem.models.client.request.WeeklySprintUpdateDto;
import com.qentelli.employeetrackingsystem.repository.ProjectRepository;
import com.qentelli.employeetrackingsystem.repository.WeekRangeRepository;
import com.qentelli.employeetrackingsystem.repository.WeeklySprintUpdateRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WeeklySprintUpdateService {

	private final WeeklySprintUpdateRepository weeklySprintUpdateRepository;
	private final ProjectRepository projectRepository;
	private final WeekRangeRepository weekRangeRepository;
	private final ModelMapper modelMapper;

	public WeeklySprintUpdate createUpdate(WeeklySprintUpdateDto dto) {
		WeeklySprintUpdate update = modelMapper.map(dto, WeeklySprintUpdate.class);

		Project project = projectRepository.findById(dto.getProjectId())
			.orElseThrow(() -> new ProjectNotFoundException("Project not found with ID: " + dto.getProjectId()));

		WeekRange week = weekRangeRepository.findById(dto.getWeeekRangeId())
			.orElseThrow(() -> new WeekRangeNotFoundException("WeekRange not found with ID: " + dto.getWeeekRangeId()));

		update.setProject(project);
		update.setWeek(week);

		// Manually map enum and non-primitive fields missing in model mapper config
		update.setEstimationHealthStatus(dto.getEstimationHealthStatus());
		update.setGroomingHealthStatus(dto.getGroomingHealthStatus());
		update.setRiskPoints(dto.getRiskPoints());
		update.setRiskStoryCounts(dto.getRiskStoryCounts());
		update.setComments(dto.getComments());
		update.setInjectionPercentage(dto.getInjectionPercentage());
		return weeklySprintUpdateRepository.save(update);
	}

	public WeeklySprintUpdate updateUpdate(Integer id, WeeklySprintUpdateDto dto) {
		WeeklySprintUpdate existing = weeklySprintUpdateRepository.findById(id)
			.orElseThrow(() -> new WeeklySprintUpdateNotFoundException("WeeklySprintUpdate not found with ID: " + id));

		Project project = projectRepository.findById(dto.getProjectId())
			.orElseThrow(() -> new ProjectNotFoundException("Project not found with ID: " + dto.getProjectId()));

		WeekRange week = weekRangeRepository.findById(dto.getWeeekRangeId())
			.orElseThrow(() -> new WeekRangeNotFoundException("WeekRange not found with ID: " + dto.getWeeekRangeId()));

		// 🔄 Update scalar fields
		existing.setAssignedPoints(dto.getAssignedPoints());
		existing.setAssignedStoriesCount(dto.getAssignedStoriesCount());
		existing.setInDevPoints(dto.getInDevPoints());
		existing.setInDevStoriesCount(dto.getInDevStoriesCount());
		existing.setInQaPoints(dto.getInQaPoints());
		existing.setInQaStoriesCount(dto.getInQaStoriesCount());
		existing.setCompletePoints(dto.getCompletePoints());
		existing.setCompleteStoriesCount(dto.getCompleteStoriesCount());
		existing.setBlockedPoints(dto.getBlockedPoints());
		existing.setBlockedStoriesCount(dto.getBlockedStoriesCount());
		existing.setCompletePercentage(dto.getCompletePercentage());
		existing.setEstimationHealth(dto.getEstimationHealth());
		existing.setGroomingHealth(dto.getGroomingHealth());
		existing.setDifficultCount1(dto.getDifficultCount1());
		existing.setDifficultCount2(dto.getDifficultCount2());
		existing.setWeeklySprintUpdateStatus(dto.isWeeklySprintUpdateStatus());

		// 🔄 Update new fields from DTO
		existing.setEstimationHealthStatus(dto.getEstimationHealthStatus());
		existing.setGroomingHealthStatus(dto.getGroomingHealthStatus());
		existing.setRiskPoints(dto.getRiskPoints());
		existing.setRiskStoryCounts(dto.getRiskStoryCounts());
		existing.setComments(dto.getComments());
		existing.setInjectionPercentage(dto.getInjectionPercentage());
		// 🔁 Update relationships
		existing.setProject(project);
		existing.setWeek(week);
		
		return weeklySprintUpdateRepository.save(existing);
	}

	public void deleteUpdate(Integer id) {
		WeeklySprintUpdate update = weeklySprintUpdateRepository.findById(id)
			.orElseThrow(() -> new WeeklySprintUpdateNotFoundException("WeeklySprintUpdate not found with ID: " + id));

		update.setWeeklySprintUpdateStatus(false);
		weeklySprintUpdateRepository.save(update);
	}
	
	public boolean setWeeklySprintUpdateEnabled(Integer weeklySprintUpdateId) {
	    WeeklySprintUpdate update = weeklySprintUpdateRepository.findById(weeklySprintUpdateId)
	            .orElseThrow(() -> new WeeklySprintUpdateNotFoundException(
	                "WeeklySprintUpdate not found with id: " + weeklySprintUpdateId));

	    update.setEnabled(true);
	    weeklySprintUpdateRepository.save(update);
	    return true;
	}

	public Page<WeeklySprintUpdate> getAllUpdates(Pageable pageable) {
		return weeklySprintUpdateRepository.findByWeeklySprintUpdateStatusTrue(pageable);
	}
	
    public List<WeeklySprintUpdate> getAllActiveUpdates() {
        return weeklySprintUpdateRepository.findByWeeklySprintUpdateStatusTrue();
    }
   
    
    public List<WeeklySprintUpdate> getAllBySprintId(Long sprintId) {
        return weeklySprintUpdateRepository.findActiveBySprintId(sprintId);
    }

    
    public List<WeeklySprintUpdate> getActiveUpdatesByWeekId(int weekId) {
        return weeklySprintUpdateRepository.findActiveByWeekId(weekId);
    }
}
