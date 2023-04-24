package com.glackfag.goalmate.repositories;

import com.glackfag.goalmate.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface PeopleRepository extends JpaRepository<Person, Integer> {
    Optional<Person> findByUserId(long chatId);

    List<Person> findByLastConverseDateBefore(Date date);
}
