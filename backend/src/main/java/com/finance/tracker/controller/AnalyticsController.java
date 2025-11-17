package com.finance.tracker.controller;

import com.finance.tracker.dto.CategoryAnalyticsDTO;
import com.finance.tracker.dto.MonthlyAnalyticsDTO;
import com.finance.tracker.dto.StatsDTO;
import com.finance.tracker.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST Controller for Analytics Module
 * Provides endpoints for financial statistics and analysis
 */
@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${cors.allowed-origins}")
public class AnalyticsController {
    
    private final AnalyticsService analyticsService;
    
    /**
     * GET /api/analytics/stats - Get dashboard statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<StatsDTO> getDashboardStats(
            @RequestAttribute("userId") String userId) {
        log.info("GET /api/analytics/stats - User ID: {}", userId);
        StatsDTO stats = analyticsService.getDashboardStats(userId);
        return ResponseEntity.ok(stats);
    }
    
    /**
     * GET /api/analytics/categories - Get current month category analytics
     */
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryAnalyticsDTO>> getCurrentMonthCategoryAnalytics(
            @RequestAttribute("userId") String userId) {
        log.info("GET /api/analytics/categories - User ID: {}", userId);
        List<CategoryAnalyticsDTO> analytics = analyticsService.getCurrentMonthCategoryAnalytics(userId);
        return ResponseEntity.ok(analytics);
    }
    
    /**
     * GET /api/analytics/categories/month/{month}/year/{year} - Get category analytics for specific month
     */
    @GetMapping("/categories/month/{month}/year/{year}")
    public ResponseEntity<List<CategoryAnalyticsDTO>> getCategoryAnalytics(
            @RequestAttribute("userId") String userId,
            @PathVariable Integer month,
            @PathVariable Integer year) {
        log.info("GET /api/analytics/categories/month/{}/year/{} - User ID: {}", month, year, userId);
        List<CategoryAnalyticsDTO> analytics = analyticsService.getCategoryAnalytics(userId, month, year);
        return ResponseEntity.ok(analytics);
    }
    
    /**
     * GET /api/analytics/monthly/expenses - Get monthly expense analytics
     */
    @GetMapping("/monthly/expenses")
    public ResponseEntity<List<MonthlyAnalyticsDTO>> getMonthlyExpenseAnalytics(
            @RequestAttribute("userId") String userId) {
        log.info("GET /api/analytics/monthly/expenses - User ID: {}", userId);
        List<MonthlyAnalyticsDTO> analytics = analyticsService.getMonthlyExpenseAnalytics(userId);
        return ResponseEntity.ok(analytics);
    }
    
    /**
     * GET /api/analytics/monthly/income - Get monthly income analytics
     */
    @GetMapping("/monthly/income")
    public ResponseEntity<List<MonthlyAnalyticsDTO>> getMonthlyIncomeAnalytics(
            @RequestAttribute("userId") String userId) {
        log.info("GET /api/analytics/monthly/income - User ID: {}", userId);
        List<MonthlyAnalyticsDTO> analytics = analyticsService.getMonthlyIncomeAnalytics(userId);
        return ResponseEntity.ok(analytics);
    }
}
