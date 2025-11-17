package com.finance.tracker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Goal Entity - Represents a financial goal
 */
@Entity
@Table(name = "goals", indexes = {
    @Index(name = "idx_user_deadline", columnList = "user_id,deadline")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false)
    private Double targetAmount;

    @Column(nullable = false)
    @Builder.Default
    private Double currentAmount = 0.0;

    @Column(nullable = false)
    private LocalDateTime deadline;

    @Column(length = 1000)
    private String description;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Many-to-One relationship with User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    /**
     * Calculate remaining amount to reach goal
     */
    public Double getRemainingAmount() {
        return targetAmount - currentAmount;
    }

    /**
     * Calculate goal progress percentage
     */
    public Double getProgressPercentage() {
        return targetAmount > 0 ? (currentAmount / targetAmount) * 100 : 0.0;
    }
}
