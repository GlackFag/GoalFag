package com.glackfag.goalmate.repositories;

import com.glackfag.goalmate.models.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GoalsRepository extends JpaRepository<Goal, Integer> {
    Optional<Goal> findById(int id);
}
