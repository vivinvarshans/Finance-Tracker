package com.finance.tracker.controller;

import com.finance.tracker.dto.TransactionRequest;
import com.finance.tracker.dto.TransactionResponse;
import com.finance.tracker.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Transaction Management Module
 * Handles CRUD operations for transactions
 */
@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${cors.allowed-origins}")
public class TransactionController {
    
    private final TransactionService transactionService;
    
    /**
     * GET /api/transactions - Get all transactions for the authenticated user
     */
    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAllTransactions(
            @RequestAttribute("userId") String userId) {
        log.info("GET /api/transactions - User ID: {}", userId);
        List<TransactionResponse> transactions = transactionService.getAllTransactions(userId);
        return ResponseEntity.ok(transactions);
    }
    
    /**
     * GET /api/transactions/{id} - Get a specific transaction
     */
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(
            @RequestAttribute("userId") String userId,
            @PathVariable String id) {
        log.info("GET /api/transactions/{} - User ID: {}", id, userId);
        TransactionResponse transaction = transactionService.getTransactionById(userId, id);
        return ResponseEntity.ok(transaction);
    }
    
    /**
     * POST /api/transactions - Create a new transaction
     */
    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @RequestAttribute("userId") String userId,
            @Valid @RequestBody TransactionRequest request) {
        log.info("POST /api/transactions - User ID: {}", userId);
        TransactionResponse transaction = transactionService.createTransaction(userId, request);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }
    
    /**
     * PUT /api/transactions/{id} - Update an existing transaction
     */
    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @RequestAttribute("userId") String userId,
            @PathVariable String id,
            @Valid @RequestBody TransactionRequest request) {
        log.info("PUT /api/transactions/{} - User ID: {}", id, userId);
        TransactionResponse transaction = transactionService.updateTransaction(userId, id, request);
        return ResponseEntity.ok(transaction);
    }
    
    /**
     * DELETE /api/transactions/{id} - Delete a transaction
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(
            @RequestAttribute("userId") String userId,
            @PathVariable String id) {
        log.info("DELETE /api/transactions/{} - User ID: {}", id, userId);
        transactionService.deleteTransaction(userId, id);
        return ResponseEntity.noContent().build();
    }
}
