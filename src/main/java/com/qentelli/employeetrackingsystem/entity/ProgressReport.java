package com.qentelli.employeetrackingsystem.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgressReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String team;
    private String tcbLead;

    private Integer assignedSP;
    private Integer completedSP;

    @Enumerated(EnumType.STRING)
    private RagStatus rag;

    private Double completionPercentage;
    
    private Boolean progressReportStatus = true; // true for active, false for inactive

    private LocalDateTime snapshotDate; // Optional tracking date
}