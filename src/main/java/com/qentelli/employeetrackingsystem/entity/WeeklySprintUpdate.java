package com.qentelli.employeetrackingsystem.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class WeeklySprintUpdate{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer weekSprintId;
 
    @ManyToOne
    @JoinColumn(name = "weekId")
    private WeekRange week;
 
    @ManyToOne
    @JoinColumn(name = "projectId")
    private Project project;
 
    private int assignedPoints;
    private int assignedStoriesCount;
    private int inDevPoints;
    private int inDevStoriesCount;
    private int inQaPoints;
    private int inQaStoriesCount;
    private int completePoints;
    private int completeStoriesCount;
    private int blockedPoints;
    private int blockedStoriesCount;
    private double completePercentage;
    private String estimationHealth;
    private String groomingHealth;//
    @Enumerated(EnumType.STRING)
    private HealthStatus estimationHealthStatus;
    @Enumerated(EnumType.STRING)
    private HealthStatus groomingHealthStatus;
    private int difficultCount1;
    private int difficultCount2;
    private int riskPoints;
    private int riskStoryCounts;
    @Column(columnDefinition = "TEXT") // for longer content in database
    private String comments; 
    private Integer injectionPercentage;
    private boolean weeklySprintUpdateStatus=true; // true means active, false means inactive;
    
    private boolean isEnabled = false; // default false, indicates not enabled
    
   
}
 