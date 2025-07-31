package com.qentelli.employeetrackingsystem.repository;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.qentelli.employeetrackingsystem.entity.WeekRange;

@Repository
public interface WeekRangeRepository extends JpaRepository<WeekRange, Integer> {
	
	   Page<WeekRange> findByWeekFromDateBetweenAndSoftDeleteFalse(LocalDate weekFromDate, LocalDate weekToDate, Pageable pageable);
}