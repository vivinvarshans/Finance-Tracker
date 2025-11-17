package com.finance.tracker.service;

import com.finance.tracker.dto.CategoryAnalyticsDTO;
import com.finance.tracker.dto.MonthlyAnalyticsDTO;
import com.finance.tracker.dto.StatsDTO;
import com.finance.tracker.exception.ResourceNotFoundException;
import com.finance.tracker.model.Transaction.TransactionType;
import com.finance.tracker.repository.TransactionRepository;
import com.finance.tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for Analytics and Statistics
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AnalyticsService {
    
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    
    /**
     * Get dashboard statistics
     */
    public StatsDTO getDashboardStats(String userId) {
        log.info("Fetching dashboard stats for user: {}", userId);
        
        verifyUserExists(userId);
        
        LocalDateTime now = LocalDateTime.now();
        YearMonth currentMonth = YearMonth.from(now);
        LocalDateTime monthStart = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime monthEnd = currentMonth.atEndOfMonth().atTime(23, 59, 59);
        
        // Calculate total income (all time)
        Double totalIncome = transactionRepository.sumAmountByUserAndTypeAndDateBetween(
                userId, TransactionType.INCOME, LocalDateTime.of(2000, 1, 1, 0, 0), now
        );
        
        // Calculate total expenses (all time)
        Double totalExpenses = transactionRepository.sumAmountByUserAndTypeAndDateBetween(
                userId, TransactionType.EXPENSE, LocalDateTime.of(2000, 1, 1, 0, 0), now
        );
        
        // Calculate monthly income
        Double monthlyIncome = transactionRepository.sumAmountByUserAndTypeAndDateBetween(
                userId, TransactionType.INCOME, monthStart, monthEnd
        );
        
        // Calculate monthly expenses
        Double monthlyExpenses = transactionRepository.sumAmountByUserAndTypeAndDateBetween(
                userId, TransactionType.EXPENSE, monthStart, monthEnd
        );
        
        // Handle null values
        totalIncome = (totalIncome != null) ? totalIncome : 0.0;
        totalExpenses = (totalExpenses != null) ? totalExpenses : 0.0;
        monthlyIncome = (monthlyIncome != null) ? monthlyIncome : 0.0;
        monthlyExpenses = (monthlyExpenses != null) ? monthlyExpenses : 0.0;
        
        Double balance = totalIncome - totalExpenses;
        
        return StatsDTO.builder()
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .balance(balance)
                .monthlyIncome(monthlyIncome)
                .monthlyExpenses(monthlyExpenses)
                .build();
    }
    
    /**
     * Get category analytics for expenses
     */
    public List<CategoryAnalyticsDTO> getCategoryAnalytics(String userId, Integer month, Integer year) {
        log.info("Fetching category analytics for user {} for {}/{}", userId, month, year);
        
        verifyUserExists(userId);
        
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);
        
        List<Object[]> results = transactionRepository.getCategoryAnalytics(
                userId, TransactionType.EXPENSE, startDate, endDate
        );
        
        // Calculate total for percentage
        Double total = results.stream()
                .mapToDouble(r -> ((Number) r[1]).doubleValue())
                .sum();
        
        return results.stream()
                .map(result -> CategoryAnalyticsDTO.builder()
                        .category((String) result[0])
                        .amount(((Number) result[1]).doubleValue())
                        .count(((Number) result[2]).longValue())
                        .percentage(total > 0 ? (((Number) result[1]).doubleValue() / total) * 100 : 0.0)
                        .build())
                .collect(Collectors.toList());
    }
    
    /**
     * Get current month category analytics
     */
    public List<CategoryAnalyticsDTO> getCurrentMonthCategoryAnalytics(String userId) {
        LocalDateTime now = LocalDateTime.now();
        return getCategoryAnalytics(userId, now.getMonthValue(), now.getYear());
    }
    
    /**
     * Get monthly analytics for expenses (last 12 months)
     */
    public List<MonthlyAnalyticsDTO> getMonthlyExpenseAnalytics(String userId) {
        log.info("Fetching monthly expense analytics for user: {}", userId);
        
        verifyUserExists(userId);
        
        List<Object[]> results = transactionRepository.getMonthlyAnalytics(
                userId, TransactionType.EXPENSE
        );
        
        return results.stream()
                .map(result -> MonthlyAnalyticsDTO.builder()
                        .month((String) result[0])
                        .amount(((Number) result[1]).doubleValue())
                        .build())
                .limit(12) // Last 12 months
                .collect(Collectors.toList());
    }
    
    /**
     * Get monthly analytics for income (last 12 months)
     */
    public List<MonthlyAnalyticsDTO> getMonthlyIncomeAnalytics(String userId) {
        log.info("Fetching monthly income analytics for user: {}", userId);
        
        verifyUserExists(userId);
        
        List<Object[]> results = transactionRepository.getMonthlyAnalytics(
                userId, TransactionType.INCOME
        );
        
        return results.stream()
                .map(result -> MonthlyAnalyticsDTO.builder()
                        .month((String) result[0])
                        .amount(((Number) result[1]).doubleValue())
                        .build())
                .limit(12) // Last 12 months
                .collect(Collectors.toList());
    }
    
    /**
     * Helper: Verify user exists
     */
    private void verifyUserExists(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
    }
}
