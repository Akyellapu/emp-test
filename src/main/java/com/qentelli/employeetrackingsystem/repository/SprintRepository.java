package com.qentelli.employeetrackingsystem.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.qentelli.employeetrackingsystem.entity.Sprint;

@Repository
public interface SprintRepository extends JpaRepository<Sprint, Long> {

	// Custom query methods can be added here if needed
	// For example, to find a sprint by its number:
	// Optional<Sprint> findBySprintNumber(String sprintNumber);

	// Or to find all sprints within a date range:
	// List<Sprint> findByFromDateBetween(LocalDate startDate, LocalDate endDate);

	Page<Sprint> findBySprintStatusTrue(Pageable pageable);

}
