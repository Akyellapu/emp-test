package com.qentelli.employeetrackingsystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ProjectResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer resourceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    private int onsite;
    private int offsite;

    private int totalOnsiteCount;    // Global sum from all entries
    private int totalOffsiteCount;   // Global sum from all entries
    private String totalRatio;       // Global ratio (e.g. "40% : 60%")
    
    // ✅ Persisted field — will be stored in DB
    private String ratio;

    // 🧮 Total is calculated, not stored
    @Transient
    public int getTotal() {
        return onsite + offsite;
    }
}