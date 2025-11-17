package com.finance.tracker.repository;

import com.finance.tracker.model.CustomCategory;
import com.finance.tracker.model.CustomCategory.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for CustomCategory entity
 */
@Repository
public interface CustomCategoryRepository extends JpaRepository<CustomCategory, String> {
    
    /**
     * Find all categories for a user
     */
    List<CustomCategory> findByUserId(String userId);
    
    /**
     * Find categories by user and type
     */
    List<CustomCategory> findByUserIdAndType(String userId, CategoryType type);
    
    /**
     * Find category by user, name, and type
     */
    Optional<CustomCategory> findByUserIdAndNameAndType(String userId, String name, CategoryType type);
    
    /**
     * Check if category exists
     */
    boolean existsByUserIdAndNameAndType(String userId, String name, CategoryType type);
}
