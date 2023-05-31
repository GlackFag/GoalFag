package com.glackfag.goalfag.bot;

import com.glackfag.goalfag.models.Goal;
import com.glackfag.goalfag.models.Person;
import com.glackfag.goalfag.models.enums.GoalState;
import com.glackfag.goalfag.models.enums.TimeframeType;
import com.glackfag.goalfag.services.GoalsService;
import com.glackfag.goalfag.services.PeopleService;
import com.glackfag.goalfag.util.AutoDeletingConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Map;

@Component
public class GoalFormer {
    private final GoalsService goalsService;
    private final PeopleService peopleService;
    private final Map<Long, Goal> storage;

    @Autowired
    public GoalFormer(GoalsService goalsService, PeopleService peopleService) {
        this.goalsService = goalsService;
        this.peopleService = peopleService;
        storage = new AutoDeletingConcurrentHashMap<>(600_000L);
    }

    @Transactional
    public void formNewGoal(Long userId, String essence) {
        Person creator = peopleService.findByUserId(userId);
        Goal goal = createGoal(essence, creator);
        creator.addGoal(goal);

        storage.put(userId, goal);
    }

    private Goal createGoal(String essence, Person creator) {
        Goal goal = new Goal(null, essence, creator);
        goal.setState(GoalState.IN_PROGRESS);
        goal.setCreationDate(Date.valueOf(LocalDate.now()));

        return goal;
    }

    public void setExpiredDate(Long userId, LocalDate date) {
        try {
            Goal goal = storage.get(userId);
            goal.setExpiredDate(Date.valueOf(date));
            goal.setTimeframe(recognizeTimeframeType(date));
        } catch (NullPointerException e) {
            throw new RuntimeException("Timeout for userId: " + userId + " expired");
        }
    }

    private TimeframeType recognizeTimeframeType(LocalDate date) {
        LocalDate currentDate = LocalDate.now();
        int yearDifference = date.getYear() - currentDate.getYear();
        int monthDifference = date.getMonthValue() - currentDate.getMonthValue();

        if (yearDifference == 0 && monthDifference <= 6)
            return TimeframeType.NEAR_TERM;
        else if (yearDifference >= 1 && yearDifference <= 2)
            return TimeframeType.MEDIUM_TERM;
        else
            return TimeframeType.LONG_TERM;
    }

    public void saveGoal(Long userId) {
        try {
            Goal goal = storage.get(userId);
            goalsService.save(goal);
            storage.remove(userId);
        } catch (NullPointerException e) {
            throw new RuntimeException("Timeout for userId: " + userId + " expired");
        }
    }
}
