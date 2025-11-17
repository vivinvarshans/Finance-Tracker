package com.finance.tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Analytics - Category data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryAnalyticsDTO {
    
    private String category;
    private Double amount;
    private Long count;
    private Double percentage;
}
