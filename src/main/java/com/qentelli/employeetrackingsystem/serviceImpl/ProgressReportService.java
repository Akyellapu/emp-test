package com.qentelli.employeetrackingsystem.serviceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.qentelli.employeetrackingsystem.entity.ProgressReport;
import com.qentelli.employeetrackingsystem.exception.ReportNotFoundException;
import com.qentelli.employeetrackingsystem.models.client.request.ProgressReportDTO;
import com.qentelli.employeetrackingsystem.repository.ProgressReportRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProgressReportService {

	private final ProgressReportRepository reportRepository;
	private final ModelMapper modelMapper;

	private double calculateCompletionPercentage(int assignedSP, int completedSP) {
		if (assignedSP <= 0)
			return 0.0;
		return Math.round((completedSP * 100.0 / assignedSP) * 10.0) / 10.0; // rounded to 1 decimal
	}

	public void create(ProgressReportDTO dto) {
		ProgressReport entity = modelMapper.map(dto, ProgressReport.class);
		entity.setCompletionPercentage(calculateCompletionPercentage(dto.getAssignedSP(), dto.getCompletedSP()));
		entity.setSnapshotDate(LocalDateTime.now());
		reportRepository.save(entity);
	}

	public List<ProgressReportDTO> getAll() {
		return reportRepository.findAll().stream().filter(ProgressReport::getProgressReportStatus).map(report -> {
			ProgressReportDTO dto = modelMapper.map(report, ProgressReportDTO.class);
			dto.setCompletionPercentage(calculateCompletionPercentage(report.getAssignedSP(), report.getCompletedSP()));
			return dto;
		}).toList();
	}

	public void update(Long id, ProgressReportDTO dto) {
		ProgressReport existingReport = reportRepository.findById(id)
				.orElseThrow(() -> new ReportNotFoundException("No progress report found for ID: " + id));

		existingReport.setTeam(dto.getTeam());
		existingReport.setTcbLead(dto.getTcbLead());
		existingReport.setAssignedSP(dto.getAssignedSP());
		existingReport.setCompletedSP(dto.getCompletedSP());
		existingReport.setRag(dto.getRag());
		existingReport
				.setCompletionPercentage(calculateCompletionPercentage(dto.getAssignedSP(), dto.getCompletedSP()));
		existingReport.setSnapshotDate(LocalDateTime.now());
		reportRepository.save(existingReport);
	}

	public void softDelete(Long id) {
		ProgressReport existingReport = reportRepository.findById(id)
				.orElseThrow(() -> new ReportNotFoundException("Cannot delete. No report with ID: " + id));

		existingReport.setProgressReportStatus(false);
		reportRepository.save(existingReport);
	}
}