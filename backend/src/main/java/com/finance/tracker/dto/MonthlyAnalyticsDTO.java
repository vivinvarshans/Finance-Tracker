package com.finance.tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Analytics - Monthly data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyAnalyticsDTO {
    
    private String month;
    private Double amount;
}
