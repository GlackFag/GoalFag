package com.glackfag.goalfag.services;

import com.glackfag.goalfag.models.Goal;
import com.glackfag.goalfag.models.Person;
import com.glackfag.goalfag.repositories.GoalsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GoalsService {
    private final GoalsRepository repository;
    private PeopleService peopleService;

    @Autowired
    public GoalsService(GoalsRepository repository, @Lazy PeopleService peopleService) {
        this.repository = repository;
        this.peopleService = peopleService;
    }

    public Goal findOne(long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Goal> findAll() {
        return repository.findAll();
    }

    boolean hasGoals(long userId) {
        Person person = peopleService.findByUserId(userId);

        return repository.findFirstByPersonId(person.getId()) != null;
    }

    @Transactional
    public void save(Goal goal) {
        repository.save(goal);
    }

    @Transactional
    public void update(Goal goal) {
        repository.save(goal);
    }

    @Transactional
    public void delete(long id) {
        repository.deleteById(id);
    }
}
