package com.qentelli.employeetrackingsystem.serviceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.qentelli.employeetrackingsystem.entity.Person;
import com.qentelli.employeetrackingsystem.entity.Project;
import com.qentelli.employeetrackingsystem.entity.Task;
import com.qentelli.employeetrackingsystem.entity.ViewReports;
import com.qentelli.employeetrackingsystem.entity.WeekRange;
import com.qentelli.employeetrackingsystem.exception.ResourceNotFoundException;
import com.qentelli.employeetrackingsystem.models.client.request.ViewReportRequest;
import com.qentelli.employeetrackingsystem.models.client.response.PaginatedResponse;
import com.qentelli.employeetrackingsystem.models.client.response.ViewReportResponse;
import com.qentelli.employeetrackingsystem.models.client.response.WeekRangeResponse;
import com.qentelli.employeetrackingsystem.repository.PersonRepository;
import com.qentelli.employeetrackingsystem.repository.ProjectRepository;
import com.qentelli.employeetrackingsystem.repository.ViewreportRepository;
import com.qentelli.employeetrackingsystem.repository.WeekRangeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ViewReportService {

	private static final String REPORT_NOT_FOUND = "Report not found";
	private static final String WEEKLY_SUMMARY_NOT_FOUND = "Weekly summary not found";
	private static final String PROJECT_NOT_FOUND = "Project not found";
	private static final String PERSON_NOT_FOUND = "Person not found";
	// private static final String USER_NOT_FOUND = "User not found";

	@Autowired
	private ViewreportRepository viewReportRepository;

	
	
	@Autowired
	private WeekRangeRepository weekRangeRepository;

	@Autowired
	private ProjectRepository projectRepository;

//    @Autowired
//    private UserRepository userRepository;

	@Autowired
	private PersonRepository personRepository;

	public ViewReportResponse saveReport(ViewReportRequest request) {
		
		WeekRange weekRange = weekRangeRepository.findById(request.getWeekId())
				.orElseThrow(() -> new RuntimeException(WEEKLY_SUMMARY_NOT_FOUND + " with id: " + request.getWeekId()));

		Project project = projectRepository.findById(request.getProjectId())
				.orElseThrow(() -> new RuntimeException(PROJECT_NOT_FOUND + " with id: " + request.getProjectId()));

//        User user = userRepository.findByEmployeeId(request.getEmployeeId())
//        	    .orElseThrow(() -> new RuntimeException("User not found"));

		Person person = personRepository.findByPersonId(request.getPersonId())
				.orElseThrow(() -> new RuntimeException(PERSON_NOT_FOUND + " with id: " + request.getPersonId()));

		ViewReports report = new ViewReports();
		report.setTaskName(request.getTaskName());
		report.setTaskStatus(request.getTaskStatus());
		report.setTaskStartDate(request.getTaskStartDate());
		report.setTaskEndDate(request.getTaskEndDate());
		report.setComments(request.getComments());
		report.setProject(project);
		// report.setUser(user);
		report.setPerson(person);
		report.setWeekRange(weekRange);
		report.setTask(new Task(request.getSummary(), request.getKeyAccomplishment(),request.getComments()));

		ViewReports saved = viewReportRepository.save(report);

		ViewReportResponse response = new ViewReportResponse();
		response.setViewReportId(saved.getViewReportId());
		
		response.setTaskName(saved.getTaskName());
		response.setTaskStatus(saved.getTaskStatus());
		response.setSummary(saved.getTask().getSummary());
		response.setKeyAccomplishment(saved.getTask().getKeyAccomplishment());
		response.setComments(saved.getComments());
		response.setProjectName(saved.getProject().getProjectName());
		// response.setEmployeeName(saved.getUser().getFirstName() + " " +
		// saved.getUser().getLastName());
		response.setPersonName(saved.getPerson().getFirstName() + " " + saved.getPerson().getLastName());
		response.setTaskStartDate(saved.getTaskStartDate());
		response.setTaskEndDate(saved.getTaskEndDate());
		response.setCreatedAt(saved.getCreatedAt());
		response.setCreatedBy(saved.getCreatedBy());
		return response;
	}

	public ViewReportResponse updateReport(ViewReportRequest request) {
		ViewReports report = viewReportRepository.findById(request.getViewReportId())
				.orElseThrow(() -> new RuntimeException(REPORT_NOT_FOUND));
		WeekRange weekRange = weekRangeRepository.findById(request.getWeekId())
				.orElseThrow(() -> new RuntimeException(WEEKLY_SUMMARY_NOT_FOUND + " with id: " + request.getWeekId()));
		Project project = projectRepository.findById(request.getProjectId())
				.orElseThrow(() -> new RuntimeException(PROJECT_NOT_FOUND + " with id: " + request.getProjectId()));
//        User user = userRepository.findByEmployeeId(request.getEmployeeId())
//        	    .orElseThrow(() -> new RuntimeException("User not found"));

		Person person = personRepository.findByPersonId(request.getPersonId())
				.orElseThrow(() -> new RuntimeException(PERSON_NOT_FOUND + " with id: " + request.getPersonId()));

		report.setTaskName(request.getTaskName());
		report.setTaskStatus(request.getTaskStatus());
		report.setTaskStartDate(request.getTaskStartDate());
		report.setTaskEndDate(request.getTaskEndDate());
	//	report.setComments(request.getComments());
		report.setProject(project);
		// report.setUser(user);
		report.setPerson(person);
		report.setWeekRange(weekRange);
		report.setTask(new Task(request.getSummary(), request.getKeyAccomplishment(),request.getComments()));

		ViewReports updated = viewReportRepository.save(report);

		ViewReportResponse response = new ViewReportResponse();
		response.setViewReportId(updated.getViewReportId());
		response.setTaskName(updated.getTaskName());
		response.setTaskStatus(updated.getTaskStatus());
		response.setSummary(updated.getTask().getSummary());
		response.setKeyAccomplishment(updated.getTask().getKeyAccomplishment());
		response.setComments(updated.getComments());
		response.setProjectName(updated.getProject().getProjectName());
		// response.setEmployeeName(updated.getUser().getFirstName() + " " +
		// updated.getUser().getLastName());
		response.setPersonName(updated.getPerson().getFirstName() + " " + updated.getPerson().getLastName());
		response.setTaskStartDate(updated.getTaskStartDate());
		response.setTaskEndDate(updated.getTaskEndDate());
		response.setCreatedAt(updated.getCreatedAt());
		response.setCreatedBy(updated.getCreatedBy());
		return response;
	}

	public ViewReportResponse getReportById(Integer id) {
		ViewReports report = viewReportRepository.findById(id)
				.orElseThrow(() -> new RuntimeException(REPORT_NOT_FOUND + " with id: " + id));
		// Map to response (reuse mapping logic)
		ViewReportResponse response = new ViewReportResponse();
		response.setViewReportId(report.getViewReportId());
		response.setWeekRange(new WeekRangeResponse(
				report.getWeekRange().getWeekId(),
				report.getWeekRange().getWeekFromDate(),
				report.getWeekRange().getWeekToDate()
		));
		response.setTaskName(report.getTaskName());
		response.setTaskStatus(report.getTaskStatus());
		response.setSummary(report.getTask().getSummary());
		response.setKeyAccomplishment(report.getTask().getKeyAccomplishment());
		response.setComments(report.getComments());
		response.setProjectName(report.getProject().getProjectName());
		// response.setEmployeeName(report.getUser().getFirstName() + " " +
		// report.getUser().getLastName());
		response.setPersonName(report.getPerson().getFirstName() + " " + report.getPerson().getLastName());
		response.setTaskStartDate(report.getTaskStartDate());
		response.setTaskEndDate(report.getTaskEndDate());
		response.setCreatedAt(report.getCreatedAt());
		response.setCreatedBy(report.getCreatedBy());
		return response;
	}
	
	public Page<ViewReportResponse> getAllReportsPaginated(Pageable pageable) {
	    Page<ViewReports> page = viewReportRepository.findAll(pageable);
	    return page.map(report -> {
	        ViewReportResponse response = new ViewReportResponse();
	        response.setWeekRange(new WeekRangeResponse(
	            report.getWeekRange().getWeekId(),
	            report.getWeekRange().getWeekFromDate(),
	            report.getWeekRange().getWeekToDate()
	        ));
	        response.setViewReportId(report.getViewReportId());
	        response.setTaskName(report.getTaskName());
	        response.setTaskStatus(report.getTaskStatus());
	        response.setSummary(report.getTask().getSummary());
	        response.setKeyAccomplishment(report.getTask().getKeyAccomplishment());
	       response.setComments(report.getComments());
	        response.setProjectName(report.getProject().getProjectName());
	        response.setPersonName(report.getPerson().getFirstName() + " " + report.getPerson().getLastName());
	        response.setTaskStartDate(report.getTaskStartDate());
	        response.setTaskEndDate(report.getTaskEndDate());
	        response.setCreatedAt(report.getCreatedAt());
	        response.setCreatedBy(report.getCreatedBy());
	        return response;
	    });
	}

	// SOFT DELETE
	public ViewReports softDeleteSummery(Integer viewReportId) {
		ViewReports viewReports = viewReportRepository.findById(viewReportId)
				.orElseThrow(() -> new ResourceNotFoundException("viewReportId not found"));
		viewReports.setSoftDelete(true);
		return viewReportRepository.save(viewReports);
	}

	public void deleteReport(Integer id) {
		ViewReports report = viewReportRepository.findById(id)
				.orElseThrow(() -> new RuntimeException(REPORT_NOT_FOUND + " with id: " + id));
		viewReportRepository.delete(report);
	}

	public PaginatedResponse<ViewReportResponse> getTasksByWeek(LocalDate fromDate, LocalDate toDate, int page, int size) {
	    List<ViewReports> reports = viewReportRepository.findByWeekRange(fromDate, toDate);

	    List<ViewReportResponse> allResponses = reports.stream().map(report -> {
	        Task task = report.getTask();
	        return new ViewReportResponse(
	            report.getViewReportId(),
	            new WeekRangeResponse(
	                report.getWeekRange().getWeekId(),
	                report.getWeekRange().getWeekFromDate(),
	                report.getWeekRange().getWeekToDate()
	            ),
	            report.getTaskName(),
	            report.getTaskStatus(),
	            task.getSummary(),
	            task.getKeyAccomplishment(),
	            report.getComments(),
	            task.getUpcomingTasks(),
	            report.getProject().getProjectName(),
	            report.getPerson().getFirstName() + " " + report.getPerson().getLastName(),
	            report.getTaskStartDate(),
	            report.getTaskEndDate(),
	            report.getCreatedAt(),
	            report.getCreatedBy()
	        );
	    }).collect(Collectors.toList());

	    int start = Math.min(page * size, allResponses.size());
	    int end = Math.min(start + size, allResponses.size());
	    List<ViewReportResponse> pagedResponses = allResponses.subList(start, end);

	    return new PaginatedResponse<>(
	        pagedResponses,
	        page,
	        size,
	        allResponses.size(),
	       (int) Math.ceil((double) allResponses.size() / size),
	        end == allResponses.size()
	    );
	}
	
	public List<ViewReportResponse> getAllReports() {
	    List<ViewReports> reports = viewReportRepository.findBySoftDeleteFalse(); // no pagination
	    return reports.stream().map(report -> {
	        ViewReportResponse response = new ViewReportResponse();
	        response.setWeekRange(new WeekRangeResponse(
	                report.getWeekRange().getWeekId(),
	                report.getWeekRange().getWeekFromDate(),
	                report.getWeekRange().getWeekToDate()
	        ));
	        response.setViewReportId(report.getViewReportId());
	        response.setTaskName(report.getTaskName());
	        response.setTaskStatus(report.getTaskStatus());
	        response.setSummary(report.getTask().getSummary());
	        response.setKeyAccomplishment(report.getTask().getKeyAccomplishment());
	        response.setComments(report.getComments());
	        response.setProjectName(report.getProject().getProjectName());
	        response.setPersonName(report.getPerson().getFirstName() + " " + report.getPerson().getLastName());
	        response.setTaskStartDate(report.getTaskStartDate());
	        response.setTaskEndDate(report.getTaskEndDate());
	        response.setCreatedAt(report.getCreatedAt());
	        response.setCreatedBy(report.getCreatedBy());
	        return response;
	    }).collect(Collectors.toList()); 
	}
		private ViewReportResponse mapToResponse(ViewReports report) {
	    return new ViewReportResponse(
	        report.getViewReportId(),
	        report.getWeekRange() != null ? new WeekRangeResponse(
	            report.getWeekRange().getWeekId(),
	            report.getWeekRange().getWeekFromDate(),
	            report.getWeekRange().getWeekToDate()) : null,
	        report.getTaskName(),
	        report.getTaskStatus(),
	        report.getTask() != null ? report.getTask().getSummary() : null,
	        report.getTask() != null ? report.getTask().getKeyAccomplishment() : null,
	        report.getComments(),
	        report.getTask() != null ? report.getTask().getUpcomingTasks() : null,
	        report.getProject() != null ? report.getProject().getProjectName() : null,
	        report.getPerson() != null ? report.getPerson().getFirstName()+" "+report.getPerson().getLastName() : null,
	        report.getTaskStartDate(),
	        report.getTaskEndDate(),
	        report.getCreatedAt(),
	        report.getCreatedBy()
	    );
	}
		public Page<ViewReports> searchViewReports(Integer personId, Integer projectId, int page, int size) {
		    Pageable pageable = PageRequest.of(page, size, Sort.by("viewReportId").descending());

		    if (personId != null && projectId != null) {
		        return viewReportRepository.findBySoftDeleteFalseAndPerson_PersonIdAndProject_ProjectId(personId, projectId, pageable);
		    } else if (personId != null) {
		        return viewReportRepository.findBySoftDeleteFalseAndPerson_PersonId(personId, pageable);
		    } else if (projectId != null) {
		        return viewReportRepository.findBySoftDeleteFalseAndProject_ProjectId(projectId, pageable);
		    } else {
		        return viewReportRepository.findBySoftDeleteFalse(pageable);
		    }
		}
}
