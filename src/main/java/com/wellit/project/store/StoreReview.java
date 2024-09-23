package com.wellit.project.store;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "store_review")
@Getter
@Setter
public class StoreReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long revId;

    private String revImg;

    private String revText;

    @Column(name = "rev_rating")
    @Min(value = 0)
    @Max(value = 5)
    private Integer revRating;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private String writer;

    @ManyToOne
    @JoinColumn(name = "stoId")
    @JsonBackReference("reviews")
    private AllStore store;
}
