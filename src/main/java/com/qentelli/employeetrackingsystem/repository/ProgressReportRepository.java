package com.qentelli.employeetrackingsystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qentelli.employeetrackingsystem.entity.ProgressReport;
import com.qentelli.employeetrackingsystem.entity.RagStatus;

public interface ProgressReportRepository extends JpaRepository<ProgressReport, Long> {
    List<ProgressReport> findByRag(RagStatus rag);
}