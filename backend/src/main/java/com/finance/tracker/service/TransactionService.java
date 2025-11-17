package com.finance.tracker.service;

import com.finance.tracker.dto.TransactionRequest;
import com.finance.tracker.dto.TransactionResponse;
import com.finance.tracker.exception.ResourceNotFoundException;
import com.finance.tracker.model.Transaction;
import com.finance.tracker.model.Transaction.TransactionType;
import com.finance.tracker.model.User;
import com.finance.tracker.repository.TransactionRepository;
import com.finance.tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for Transaction Management
 * Implements business logic with validation and exception handling
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final BudgetService budgetService;
    
    /**
     * Get all transactions for a user
     */
    @Transactional(readOnly = true)
    public List<TransactionResponse> getAllTransactions(String userId) {
        log.info("Fetching all transactions for user: {}", userId);
        
        // Verify user exists
        verifyUserExists(userId);
        
        List<Transaction> transactions = transactionRepository.findByUserIdOrderByDateDesc(userId);
        log.info("Found {} transactions for user: {}", transactions.size(), userId);
        
        return transactions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get transaction by ID
     */
    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(String userId, String transactionId) {
        log.info("Fetching transaction {} for user: {}", transactionId, userId);
        
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", transactionId));
        
        // Verify transaction belongs to user
        if (!transaction.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Transaction", "id", transactionId);
        }
        
        return convertToResponse(transaction);
    }
    
    /**
     * Create a new transaction
     */
    public TransactionResponse createTransaction(String userId, TransactionRequest request) {
        log.info("Creating {} transaction for user: {}", request.getType(), userId);
        
        // Verify user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        // Create transaction
        Transaction transaction = Transaction.builder()
                .amount(request.getAmount())
                .description(request.getDescription())
                .category(request.getCategory())
                .type(request.getType())
                .date(request.getDate())
                .user(user)
                .build();
        
        transaction = transactionRepository.save(transaction);
        log.info("Transaction created with ID: {}", transaction.getId());
        
        // Update budget if it's an expense
        if (request.getType() == TransactionType.EXPENSE) {
            budgetService.updateBudgetSpent(userId, request.getCategory(), 
                    request.getDate().getMonthValue(), request.getDate().getYear());
        }
        
        return convertToResponse(transaction);
    }
    
    /**
     * Update an existing transaction
     */
    public TransactionResponse updateTransaction(String userId, String transactionId, TransactionRequest request) {
        log.info("Updating transaction {} for user: {}", transactionId, userId);
        
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", transactionId));
        
        // Verify transaction belongs to user
        if (!transaction.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Transaction", "id", transactionId);
        }
        
        // Store old values for budget recalculation
        String oldCategory = transaction.getCategory();
        TransactionType oldType = transaction.getType();
        int oldMonth = transaction.getDate().getMonthValue();
        int oldYear = transaction.getDate().getYear();
        
        // Update transaction
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setCategory(request.getCategory());
        transaction.setType(request.getType());
        transaction.setDate(request.getDate());
        
        transaction = transactionRepository.save(transaction);
        log.info("Transaction updated successfully: {}", transactionId);
        
        // Update budgets if needed
        if (oldType == TransactionType.EXPENSE) {
            budgetService.updateBudgetSpent(userId, oldCategory, oldMonth, oldYear);
        }
        if (request.getType() == TransactionType.EXPENSE) {
            budgetService.updateBudgetSpent(userId, request.getCategory(), 
                    request.getDate().getMonthValue(), request.getDate().getYear());
        }
        
        return convertToResponse(transaction);
    }
    
    /**
     * Delete a transaction
     */
    public void deleteTransaction(String userId, String transactionId) {
        log.info("Deleting transaction {} for user: {}", transactionId, userId);
        
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", transactionId));
        
        // Verify transaction belongs to user
        if (!transaction.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Transaction", "id", transactionId);
        }
        
        // Store values for budget recalculation
        String category = transaction.getCategory();
        TransactionType type = transaction.getType();
        int month = transaction.getDate().getMonthValue();
        int year = transaction.getDate().getYear();
        
        transactionRepository.delete(transaction);
        log.info("Transaction deleted successfully: {}", transactionId);
        
        // Update budget if it was an expense
        if (type == TransactionType.EXPENSE) {
            budgetService.updateBudgetSpent(userId, category, month, year);
        }
    }
    
    /**
     * Get transactions by date range
     */
    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByDateRange(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching transactions for user {} from {} to {}", userId, startDate, endDate);
        
        verifyUserExists(userId);
        
        List<Transaction> transactions = transactionRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
        
        return transactions.stream()
                .map(this::convertToResponse)
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
    
    /**
     * Helper: Convert Transaction entity to Response DTO
     */
    private TransactionResponse convertToResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .category(transaction.getCategory())
                .type(transaction.getType())
                .date(transaction.getDate())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }
}
