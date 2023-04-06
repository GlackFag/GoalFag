package com.glackfag.goalmate.models;

import com.glackfag.goalmate.bot.action.Action;
import com.glackfag.goalmate.util.AutoDeletingHashMap;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Entity
@Table(name = "person")
public class Person {
    @Transient
    private final int UNINITIALIZED_ID = -1;

    @Getter
    @Setter
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Setter
    @Getter
    @Column(name = "user_id")
    private String userId;

    @Getter
    @Setter
    @OneToMany(mappedBy = "creator")
    private List<Goal> goals;

    @Getter
    @Setter
    @Transient
    private int chatId = UNINITIALIZED_ID;

    @Transient
    private static final Map<Long, Action> lastAction = new AutoDeletingHashMap<>(600_000L);

    public static void updateLastAction(Long userId, Action action){
        lastAction.put(userId, action);
    }

    public static Action getLastAction(Long userId){
        return lastAction.get(userId);
    }

    public Person(String userId) {
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
                ", userId='" + userId + '\'' +
                ", goals=" + goals +
                '}';
    }
}
