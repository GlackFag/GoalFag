package com.glackfag.goalmate.services;

import com.glackfag.goalmate.models.Person;
import com.glackfag.goalmate.repositories.PeopleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PeopleService {
    private final PeopleRepository repository;
    private final PasswordEncoder encoder;

    @Autowired
    public PeopleService(PeopleRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    public Person findByUserId(long userId){
        String encrypted = encoder.encode(Long.toString(userId));
        return findByEncryptedUserId(encrypted);
    }

    public Person findByEncryptedUserId(String encrypted){
        return repository.findByUserId(encrypted).orElse(null);
    }

    public boolean isUserIdRegistered(long userId){
        return findByUserId(userId) != null;
    }

    @Transactional
    public void save(Person person){
        repository.save(person);
    }

}
