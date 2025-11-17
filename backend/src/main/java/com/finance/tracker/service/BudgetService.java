package com.finance.tracker.service;

import com.finance.tracker.dto.BudgetRequest;
import com.finance.tracker.dto.BudgetResponse;
import com.finance.tracker.exception.ResourceNotFoundException;
import com.finance.tracker.model.Budget;
import com.finance.tracker.model.Transaction.TransactionType;
import com.finance.tracker.model.User;
import com.finance.tracker.repository.BudgetRepository;
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
 * Service layer for Budget Management
 * Implements business logic for budget operations and analysis
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BudgetService {
    
    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    
    /**
     * Get all budgets for a user
     */
    @Transactional(readOnly = true)
    public List<BudgetResponse> getAllBudgets(String userId) {
        log.info("Fetching all budgets for user: {}", userId);
        
        verifyUserExists(userId);
        
        List<Budget> budgets = budgetRepository.findByUserId(userId);
        log.info("Found {} budgets for user: {}", budgets.size(), userId);
        
        return budgets.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get budgets for specific month and year
     */
    @Transactional(readOnly = true)
    public List<BudgetResponse> getBudgetsByMonthAndYear(String userId, Integer month, Integer year) {
        log.info("Fetching budgets for user {} for {}/{}", userId, month, year);
        
        verifyUserExists(userId);
        
        List<Budget> budgets = budgetRepository.findByUserIdAndMonthAndYear(userId, month, year);
        
        return budgets.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get current month budgets
     */
    @Transactional(readOnly = true)
    public List<BudgetResponse> getCurrentMonthBudgets(String userId) {
        LocalDateTime now = LocalDateTime.now();
        return getBudgetsByMonthAndYear(userId, now.getMonthValue(), now.getYear());
    }
    
    /**
     * Get budget by ID
     */
    @Transactional(readOnly = true)
    public BudgetResponse getBudgetById(String userId, String budgetId) {
        log.info("Fetching budget {} for user: {}", budgetId, userId);
        
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", "id", budgetId));
        
        // Verify budget belongs to user
        if (!budget.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Budget", "id", budgetId);
        }
        
        return convertToResponse(budget);
    }
    
    /**
     * Create or update a budget
     */
    public BudgetResponse createOrUpdateBudget(String userId, BudgetRequest request) {
        log.info("Creating/updating budget for user {} - category: {}, month: {}/{}", 
                userId, request.getCategory(), request.getMonth(), request.getYear());
        
        // Verify user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        // Check if budget already exists
        Budget budget = budgetRepository.findByUserIdAndCategoryAndMonthAndYear(
                userId, request.getCategory(), request.getMonth(), request.getYear()
        ).orElse(null);
        
        if (budget != null) {
            // Update existing budget
            log.info("Updating existing budget: {}", budget.getId());
            budget.setAmount(request.getAmount());
        } else {
            // Create new budget
            log.info("Creating new budget");
            budget = Budget.builder()
                    .category(request.getCategory())
                    .amount(request.getAmount())
                    .month(request.getMonth())
                    .year(request.getYear())
                    .user(user)
                    .build();
        }
        
        Budget savedBudget = budgetRepository.save(budget);
        
        // Calculate and update spent amount
        updateBudgetSpent(userId, request.getCategory(), request.getMonth(), request.getYear());
        
        // Refresh budget to get updated spent amount
        String budgetId = savedBudget.getId();
        Budget refreshedBudget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", "id", budgetId));
        
        log.info("Budget saved successfully: {}", refreshedBudget.getId());
        return convertToResponse(refreshedBudget);
    }
    
    /**
     * Update a budget
     */
    public BudgetResponse updateBudget(String userId, String budgetId, BudgetRequest request) {
        log.info("Updating budget {} for user: {}", budgetId, userId);
        
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", "id", budgetId));
        
        // Verify budget belongs to user
        if (!budget.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Budget", "id", budgetId);
        }
        
        // Update budget
        budget.setCategory(request.getCategory());
        budget.setAmount(request.getAmount());
        budget.setMonth(request.getMonth());
        budget.setYear(request.getYear());
        
        budget = budgetRepository.save(budget);
        
        // Recalculate spent amount
        updateBudgetSpent(userId, request.getCategory(), request.getMonth(), request.getYear());
        
        // Refresh budget
        budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", "id", budgetId));
        
        log.info("Budget updated successfully: {}", budgetId);
        return convertToResponse(budget);
    }
    
    /**
     * Delete a budget
     */
    public void deleteBudget(String userId, String budgetId) {
        log.info("Deleting budget {} for user: {}", budgetId, userId);
        
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", "id", budgetId));
        
        // Verify budget belongs to user
        if (!budget.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Budget", "id", budgetId);
        }
        
        budgetRepository.delete(budget);
        log.info("Budget deleted successfully: {}", budgetId);
    }
    
    /**
     * Update spent amount for a budget based on actual transactions
     */
    public void updateBudgetSpent(String userId, String category, Integer month, Integer year) {
        log.info("Updating spent amount for budget - user: {}, category: {}, month: {}/{}", 
                userId, category, month, year);
        
        // Find the budget
        Budget budget = budgetRepository.findByUserIdAndCategoryAndMonthAndYear(
                userId, category, month, year
        ).orElse(null);
        
        if (budget == null) {
            log.info("No budget found for category {} in {}/{}", category, month, year);
            return;
        }
        
        // Calculate start and end dates for the month
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);
        
        // Calculate total expenses for this category in this month
        Double spent = transactionRepository.sumAmountByUserAndTypeAndCategoryAndDateBetween(
                userId, TransactionType.EXPENSE, category, startDate, endDate
        );
        
        budget.setSpent(spent != null ? spent : 0.0);
        budgetRepository.save(budget);
        
        log.info("Budget spent amount updated: {} for category {}", spent, category);
    }
    
    /**
     * Helper: Verify user exists
     */
    private void verifyUserExists(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
    }
    
    /**
     * Helper: Convert Budget entity to Response DTO
     */
    private BudgetResponse convertToResponse(Budget budget) {
        return BudgetResponse.builder()
                .id(budget.getId())
                .category(budget.getCategory())
                .amount(budget.getAmount())
                .spent(budget.getSpent())
                .remaining(budget.getRemaining())
                .percentageUsed(budget.getPercentageUsed())
                .month(budget.getMonth())
                .year(budget.getYear())
                .build();
    }
}
