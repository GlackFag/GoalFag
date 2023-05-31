package com.glackfag.goalfag.models;

import com.glackfag.goalfag.bot.action.Action;
import com.glackfag.goalfag.util.AutoDeletingConcurrentHashMap;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "person")
public class Person {
    @Getter
    @Setter
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Setter
    @Getter
    @Column(name = "user_id")
    private Long userId;

    @Setter
    @Getter
    @Column(name = "chat_id")
    private Long chatId;

    @Getter
    @Setter
    @OneToMany(mappedBy = "creator")
    private List<Goal> goals;

    @Setter
    @Getter
    @Column(name = "last_converse_date")
    private Date lastConverseDate;

    @Transient
    private static final Map<Long, Action> lastAction = new AutoDeletingConcurrentHashMap<>(600_000L);

    public static void updateLastAction(Long userId, Action action){
        lastAction.put(userId, action);
    }

    public static Action getLastAction(Long userId){
        return lastAction.get(userId);
    }

    public Person(Long userId) {
        this.userId = userId;
    }

    public Person() {
    }


    public void addGoal(Goal goal) {
        getGoals().add(goal);
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", userId=" + userId +
                '}';
    }
}
