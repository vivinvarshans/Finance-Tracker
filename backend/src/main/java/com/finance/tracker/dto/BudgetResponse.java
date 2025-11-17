package com.finance.tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Budget response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetResponse {
    
    private String id;
    private String category;
    private Double amount;
    private Double spent;
    private Double remaining;
    private Double percentageUsed;
    private Integer month;
    private Integer year;
}
