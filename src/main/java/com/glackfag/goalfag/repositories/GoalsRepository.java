package com.glackfag.goalfag.repositories;

import com.glackfag.goalfag.models.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface GoalsRepository extends JpaRepository<Goal, Long> {
    Goal findFirstByPersonId(long personId);

    void deleteById(long id);
}
