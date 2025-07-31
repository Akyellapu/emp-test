package com.qentelli.employeetrackingsystem.models.client.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qentelli.employeetrackingsystem.entity.RagStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ProgressReportDTO {
    private String team;
    private String tcbLead;
    private Integer assignedSP;
    private Integer completedSP;
    private RagStatus rag;
    private Double completionPercentage;
    private Boolean progressReportStatus = true; // true for active, false for inactive

}