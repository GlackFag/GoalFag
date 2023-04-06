package com.glackfag.goalmate.services;

import com.glackfag.goalmate.models.Goal;
import com.glackfag.goalmate.repositories.GoalsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GoalsService {

    private final GoalsRepository repository;

    @Autowired
    public GoalsService(GoalsRepository repository) {
        this.repository = repository;
    }

    public Goal findOne(int id){
        return repository.findById(id).orElse(null);
    }

    public List<Goal> findAll(){
        return repository.findAll();
    }

    @Transactional
    public void save(Goal goal){
        repository.save(goal);
    }

    @Transactional
    public void update(Goal goal){
        repository.save(goal);
    }
}
