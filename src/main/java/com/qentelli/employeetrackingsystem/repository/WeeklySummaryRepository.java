package com.qentelli.employeetrackingsystem.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.qentelli.employeetrackingsystem.entity.WeeklySummary;

@Repository
public interface WeeklySummaryRepository extends JpaRepository<WeeklySummary, Integer> {
	Optional<WeeklySummary> findByWeekStartDateAndWeekEndDate(LocalDate startDate, LocalDate endDate);
	Page<WeeklySummary> findAllBySoftDeleteFalse(Pageable pageable);
	Page<WeeklySummary> findByWeekStartDateBetweenAndSoftDeleteFalse(LocalDate start, LocalDate end, Pageable pageable);
}
