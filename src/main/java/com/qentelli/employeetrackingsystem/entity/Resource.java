package com.qentelli.employeetrackingsystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resourceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceType resourceType; 

    @Enumerated(EnumType.STRING)
    private TechStack techStack; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project; 

    private int onsite;
    private int offsite;

    private int totalOnsiteCount;    
    private int totalOffsiteCount;   
    private String totalRatio;       
    private String ratio;

    @Transient
    public int getTotal() {
        return onsite + offsite;
    }
}



