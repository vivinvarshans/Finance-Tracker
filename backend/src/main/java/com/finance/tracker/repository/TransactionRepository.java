package com.finance.tracker.repository;

import com.finance.tracker.model.Transaction;
import com.finance.tracker.model.Transaction.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Transaction entity
 * Includes custom queries for transaction analytics
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    
    /**
     * Find all transactions for a specific user, ordered by date descending
     */
    List<Transaction> findByUserIdOrderByDateDesc(String userId);
    
    /**
     * Find transactions by user and type
     */
    List<Transaction> findByUserIdAndType(String userId, TransactionType type);
    
    /**
     * Find transactions by user and date range
     */
    List<Transaction> findByUserIdAndDateBetween(String userId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find transactions by user, category and date range
     */
    List<Transaction> findByUserIdAndCategoryAndDateBetween(
        String userId, 
        String category, 
        LocalDateTime startDate, 
        LocalDateTime endDate
    );
    
    /**
     * Calculate total amount by user and type within date range
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0.0) FROM Transaction t " +
           "WHERE t.user.id = :userId AND t.type = :type " +
           "AND t.date BETWEEN :startDate AND :endDate")
    Double sumAmountByUserAndTypeAndDateBetween(
        @Param("userId") String userId,
        @Param("type") TransactionType type,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    /**
     * Calculate total amount by user, type, and category within date range
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0.0) FROM Transaction t " +
           "WHERE t.user.id = :userId AND t.type = :type AND t.category = :category " +
           "AND t.date BETWEEN :startDate AND :endDate")
    Double sumAmountByUserAndTypeAndCategoryAndDateBetween(
        @Param("userId") String userId,
        @Param("type") TransactionType type,
        @Param("category") String category,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    /**
     * Get category-wise expenses for analytics
     */
    @Query("SELECT t.category, SUM(t.amount) as total, COUNT(t) as count " +
           "FROM Transaction t " +
           "WHERE t.user.id = :userId AND t.type = :type " +
           "AND t.date BETWEEN :startDate AND :endDate " +
           "GROUP BY t.category " +
           "ORDER BY total DESC")
    List<Object[]> getCategoryAnalytics(
        @Param("userId") String userId,
        @Param("type") TransactionType type,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    /**
     * Get monthly transaction summary
     */
    @Query("SELECT FUNCTION('TO_CHAR', t.date, 'YYYY-MM') as month, SUM(t.amount) as total " +
           "FROM Transaction t " +
           "WHERE t.user.id = :userId AND t.type = :type " +
           "GROUP BY FUNCTION('TO_CHAR', t.date, 'YYYY-MM') " +
           "ORDER BY month DESC")
    List<Object[]> getMonthlyAnalytics(
        @Param("userId") String userId,
        @Param("type") TransactionType type
    );
}
