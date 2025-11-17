package com.finance.tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Dashboard Statistics
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatsDTO {
    
    private Double totalIncome;
    private Double totalExpenses;
    private Double balance;
    private Double monthlyIncome;
    private Double monthlyExpenses;
}
