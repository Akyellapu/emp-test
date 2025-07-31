package com.qentelli.employeetrackingsystem.serviceImpl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qentelli.employeetrackingsystem.entity.Project;
import com.qentelli.employeetrackingsystem.entity.Release;
import com.qentelli.employeetrackingsystem.entity.Sprint;
import com.qentelli.employeetrackingsystem.entity.WeekRange;
import com.qentelli.employeetrackingsystem.exception.ReleaseNotFoundException;
import com.qentelli.employeetrackingsystem.exception.ResourceNotFoundException;
import com.qentelli.employeetrackingsystem.models.client.request.ReleaseRequestDTO;
import com.qentelli.employeetrackingsystem.models.client.response.ReleaseResponseDTO;
import com.qentelli.employeetrackingsystem.repository.ProjectRepository;
import com.qentelli.employeetrackingsystem.repository.ReleaseRepository;
import com.qentelli.employeetrackingsystem.repository.SprintRepository;
import com.qentelli.employeetrackingsystem.repository.WeekRangeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ReleaseService {

	private static final Logger logger = LoggerFactory.getLogger(ReleaseService.class);

	private final ReleaseRepository releaseRepository;
	private final ProjectRepository projectRepository;
	private final WeekRangeRepository weekRangeRepository;
	private final SprintRepository sprintRepository;

	// ✅ Create
	public Release createRelease(ReleaseRequestDTO dto) {
		logger.info("Creating release for projectId={}, weekId={}", dto.getProjectId(), dto.getWeekId());

		Project project = projectRepository.findById(dto.getProjectId())
				.orElseThrow(() -> new ResourceNotFoundException("Project not found"));
		WeekRange week = weekRangeRepository.findById(dto.getWeekId())
				.orElseThrow(() -> new ResourceNotFoundException("WeekRange not found"));
		Sprint sprint = sprintRepository.findById(dto.getSprintId())
				.orElseThrow(() -> new ResourceNotFoundException("Sprint not found"));

		Release release = new Release();
		release.setProject(project);
		release.setWeek(week);
		release.setMajor(dto.getMajor());
		release.setMinor(dto.getMinor());
		release.setIncidentCreated(dto.getIncidentCreated());
		release.setReleaseInformation(dto.getReleaseInformation());
		release.setSprint(sprint);

		Release savedRelease = releaseRepository.save(release);
		logger.info("Release created with ID: {}", savedRelease.getReleaseId());

		return savedRelease;
	}

	// ✅ Read All
	public List<ReleaseResponseDTO> getAllReleases() {
		logger.info("Fetching all releases");
		List<ReleaseResponseDTO> list = releaseRepository.findAll().stream().map(this::mapToResponseDTO).toList();
		logger.info("Total releases found: {}", list.size());
		return list;
	}

	// ✅ Paginated Read
	public Page<ReleaseResponseDTO> getPaginatedReleases(Pageable pageable) {
		logger.info("Fetching paginated release entries");
		Page<Release> releasePage = releaseRepository.findAll(pageable);
		Page<ReleaseResponseDTO> dtoPage = releasePage.map(this::mapToResponseDTO);
		logger.info("Paginated releases: page={}, size={}, totalElements={}", dtoPage.getNumber(), dtoPage.getSize(),
				dtoPage.getTotalElements());
		return dtoPage;
	}

	// ✅ Read by Week ID
	public List<ReleaseResponseDTO> getReleasesByWeekId(int weekId) {
		logger.info("Fetching releases for weekId: {}", weekId);

		List<Release> releases = releaseRepository.findByWeek_WeekId(weekId);

		return releases.stream().map(this::mapToResponseDTO).toList();
	}

	// ✅ Read by Sprint ID

	public List<ReleaseResponseDTO> getReleasesBySprintId(int sprintId) {
		logger.info("Fetching releases for sprintId: {}", sprintId);

		List<Release> releases = releaseRepository.findBySprint_SprintId(sprintId);

		return releases.stream().map(this::mapToResponseDTO).toList();
	}

	

	// ✅ Update
    public Release updateRelease(Long id, ReleaseRequestDTO dto) {
        logger.info("Updating release with ID: {}", id);
        Release release = releaseRepository.findById(id)
                .orElseThrow(() -> new ReleaseNotFoundException("Release not found with ID: " + id));

        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new ReleaseNotFoundException("Project not found with ID: " + dto.getProjectId()));
        WeekRange week = weekRangeRepository.findById(dto.getWeekId())
                .orElseThrow(() -> new ReleaseNotFoundException("WeekRange not found with ID: " + dto.getWeekId()));
        Sprint sprint = sprintRepository.findById(dto.getSprintId())
                .orElseThrow(() -> new ReleaseNotFoundException("Sprint not found with ID: " + dto.getSprintId()));

        release.setProject(project);
        release.setWeek(week);
        release.setMajor(dto.getMajor());
        release.setMinor(dto.getMinor());
        release.setIncidentCreated(dto.getIncidentCreated());
        release.setReleaseInformation(dto.getReleaseInformation());
        release.setSprint(sprint);

        Release updatedRelease = releaseRepository.save(release);
        logger.info("Release updated successfully. ID: {}", updatedRelease.getReleaseId());
        return updatedRelease;
    }

    // ✅ Delete
    public void deleteRelease(Long id) {
        logger.info("Deleting release ID: {}", id);
        if (!releaseRepository.existsById(id)) {
            throw new ReleaseNotFoundException("Release not found with ID: " + id);
        }
        releaseRepository.deleteById(id);
        logger.info("Release deleted. ID: {}", id);
    }

	// Mapper
	private ReleaseResponseDTO mapToResponseDTO(Release release) {
		return new ReleaseResponseDTO(
			    release.getProject().getProjectName(),
			    release.getMajor(), 
			    release.getMinor(),
			    release.getIncidentCreated(), 
			    release.getReleaseInformation(),
			    release.getWeek() != null ? release.getWeek().getWeekId() : 0,
			    release.getSprint() != null ? release.getSprint().getSprintId() : 0, 
			    release.getReleaseId(),
			    release.getProject().getProjectId()); 
	}
}