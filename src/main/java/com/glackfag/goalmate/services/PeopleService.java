package com.glackfag.goalmate.services;

import com.glackfag.goalmate.models.Person;
import com.glackfag.goalmate.repositories.PeopleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PeopleService {
    private final PeopleRepository repository;

    @Autowired
    public PeopleService(PeopleRepository repository) {
        this.repository = repository;
    }

    public Person findByUserId(long userId){
        return repository.findByUserId(userId).orElse(null);
    }

    public boolean isUserIdRegistered(long userId){
        return findByUserId(userId) != null;
    }

    @Transactional
    public void save(Person person){
        repository.save(person);
    }

}
