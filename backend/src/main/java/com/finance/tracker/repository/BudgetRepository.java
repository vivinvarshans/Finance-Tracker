package com.finance.tracker.repository;

import com.finance.tracker.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Budget entity
 */
@Repository
public interface BudgetRepository extends JpaRepository<Budget, String> {
    
    /**
     * Find all budgets for a user
     */
    List<Budget> findByUserId(String userId);
    
    /**
     * Find budgets for a specific month and year
     */
    List<Budget> findByUserIdAndMonthAndYear(String userId, Integer month, Integer year);
    
    /**
     * Find budget by user, category, month, and year
     */
    Optional<Budget> findByUserIdAndCategoryAndMonthAndYear(
        String userId, 
        String category, 
        Integer month, 
        Integer year
    );
    
    /**
     * Find budgets by user and year
     */
    List<Budget> findByUserIdAndYear(String userId, Integer year);
    
    /**
     * Check if budget exists for category in given month/year
     */
    boolean existsByUserIdAndCategoryAndMonthAndYear(
        String userId, 
        String category, 
        Integer month, 
        Integer year
    );
    
    /**
     * Get budget comparison data
     */
    @Query("SELECT b.category, b.amount, b.spent " +
           "FROM Budget b " +
           "WHERE b.user.id = :userId AND b.month = :month AND b.year = :year " +
           "ORDER BY b.category")
    List<Object[]> getBudgetComparisonData(
        @Param("userId") String userId,
        @Param("month") Integer month,
        @Param("year") Integer year
    );
}
