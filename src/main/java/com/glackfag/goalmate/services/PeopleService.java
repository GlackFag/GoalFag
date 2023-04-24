package com.glackfag.goalmate.services;

import com.glackfag.goalmate.models.Person;
import com.glackfag.goalmate.repositories.PeopleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class PeopleService {
    private final PeopleRepository repository;
    private GoalsService goalsService;

    @Autowired
    public PeopleService(PeopleRepository repository, @Lazy GoalsService goalsService) {
        this.repository = repository;
        this.goalsService = goalsService;
    }

    public Person findByUserId(long userId){
        return repository.findByUserId(userId).orElse(null);
    }

    public boolean isUserIdRegistered(long userId){
        return findByUserId(userId) != null;
    }

    public boolean hasGoals(long userId){
        return goalsService.hasGoals(userId);
    }

    public List<Person> findNotWrittenLongTime(){
        Date yearAgo = Date.valueOf(LocalDate.now().minusDays(364));

        return repository.findByLastConverseDateBefore(yearAgo);
    }

    @Transactional
    public void updateLastConverseDate(long userId){
        Person person = findByUserId(userId);
        person.setLastConverseDate(Date.valueOf(LocalDate.now()));
    }

    @Transactional
    public void save(Person person){
        repository.save(person);
    }

    @Transactional
    public void delete(Person person){
        repository.delete(person);
    }

}
