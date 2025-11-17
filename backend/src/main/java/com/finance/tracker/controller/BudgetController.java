package com.finance.tracker.controller;

import com.finance.tracker.dto.BudgetRequest;
import com.finance.tracker.dto.BudgetResponse;
import com.finance.tracker.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Budget Management Module
 * Handles CRUD operations for budgets
 */
@RestController
@RequestMapping("/budgets")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${cors.allowed-origins}")
public class BudgetController {
    
    private final BudgetService budgetService;
    
    /**
     * GET /api/budgets - Get all budgets for the authenticated user
     */
    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getAllBudgets(
            @RequestAttribute("userId") String userId) {
        log.info("GET /api/budgets - User ID: {}", userId);
        List<BudgetResponse> budgets = budgetService.getAllBudgets(userId);
        return ResponseEntity.ok(budgets);
    }
    
    /**
     * GET /api/budgets/current - Get current month budgets
     */
    @GetMapping("/current")
    public ResponseEntity<List<BudgetResponse>> getCurrentMonthBudgets(
            @RequestAttribute("userId") String userId) {
        log.info("GET /api/budgets/current - User ID: {}", userId);
        List<BudgetResponse> budgets = budgetService.getCurrentMonthBudgets(userId);
        return ResponseEntity.ok(budgets);
    }
    
    /**
     * GET /api/budgets/month/{month}/year/{year} - Get budgets for specific month
     */
    @GetMapping("/month/{month}/year/{year}")
    public ResponseEntity<List<BudgetResponse>> getBudgetsByMonthAndYear(
            @RequestAttribute("userId") String userId,
            @PathVariable Integer month,
            @PathVariable Integer year) {
        log.info("GET /api/budgets/month/{}/year/{} - User ID: {}", month, year, userId);
        List<BudgetResponse> budgets = budgetService.getBudgetsByMonthAndYear(userId, month, year);
        return ResponseEntity.ok(budgets);
    }
    
    /**
     * GET /api/budgets/{id} - Get a specific budget
     */
    @GetMapping("/{id}")
    public ResponseEntity<BudgetResponse> getBudgetById(
            @RequestAttribute("userId") String userId,
            @PathVariable String id) {
        log.info("GET /api/budgets/{} - User ID: {}", id, userId);
        BudgetResponse budget = budgetService.getBudgetById(userId, id);
        return ResponseEntity.ok(budget);
    }
    
    /**
     * POST /api/budgets - Create or update a budget
     */
    @PostMapping
    public ResponseEntity<BudgetResponse> createOrUpdateBudget(
            @RequestAttribute("userId") String userId,
            @Valid @RequestBody BudgetRequest request) {
        log.info("POST /api/budgets - User ID: {}", userId);
        BudgetResponse budget = budgetService.createOrUpdateBudget(userId, request);
        return new ResponseEntity<>(budget, HttpStatus.CREATED);
    }
    
    /**
     * PUT /api/budgets/{id} - Update an existing budget
     */
    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponse> updateBudget(
            @RequestAttribute("userId") String userId,
            @PathVariable String id,
            @Valid @RequestBody BudgetRequest request) {
        log.info("PUT /api/budgets/{} - User ID: {}", id, userId);
        BudgetResponse budget = budgetService.updateBudget(userId, id, request);
        return ResponseEntity.ok(budget);
    }
    
    /**
     * DELETE /api/budgets/{id} - Delete a budget
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(
            @RequestAttribute("userId") String userId,
            @PathVariable String id) {
        log.info("DELETE /api/budgets/{} - User ID: {}", id, userId);
        budgetService.deleteBudget(userId, id);
        return ResponseEntity.noContent().build();
    }
}
