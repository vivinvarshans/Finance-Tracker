package com.finance.tracker.repository;

import com.finance.tracker.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Goal entity
 */
@Repository
public interface GoalRepository extends JpaRepository<Goal, String> {
    
    /**
     * Find all goals for a user, ordered by deadline
     */
    List<Goal> findByUserIdOrderByDeadlineAsc(String userId);
    
    /**
     * Find active goals (deadline not passed)
     */
    List<Goal> findByUserIdAndDeadlineAfterOrderByDeadlineAsc(String userId, LocalDateTime currentDate);
    
    /**
     * Find completed goals (current amount >= target amount)
     */
    @Query("SELECT g FROM Goal g WHERE g.user.id = :userId AND g.currentAmount >= g.targetAmount")
    List<Goal> findCompletedGoalsByUserId(@Param("userId") String userId);
}
