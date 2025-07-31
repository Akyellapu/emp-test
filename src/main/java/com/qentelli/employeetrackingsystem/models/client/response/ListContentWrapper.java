package com.qentelli.employeetrackingsystem.models.client.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListContentWrapper<T> {
    private int totalRecords;
    private List<T> content;
}