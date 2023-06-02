package com.glackfag.goalfag.statistics;

import com.glackfag.goalfag.models.Goal;
import com.glackfag.goalfag.models.Person;
import com.glackfag.goalfag.models.enums.GoalState;
import com.glackfag.goalfag.services.PeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DatasetFormer {
    private final PeopleService peopleService;

    @Autowired
    public DatasetFormer(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @Transactional
    public Map<String, Integer> formAllTimePieDatasetByUserId(long userId) {
        Person person = peopleService.findByUserId(userId);
        List<Goal> goalList = person.getGoals();

        return formDataset(goalList);
    }

    @Transactional
    public Map<String, Integer> formYearPieDatasetByUserId(long userId) {
        Person person = peopleService.findByUserId(userId);
        List<Goal> goalList = person.getGoals();

        goalList.removeIf(x -> !isGoalForThisYear(x));

        return formDataset(goalList);
    }

    private static boolean isGoalForThisYear(Goal goal) {
        int currentYear = LocalDate.now().getYear();
        return goal.getCreationDate().getYear() == currentYear &&
                goal.getExpiredDate().getYear() == currentYear;
    }

    public Map<String, Integer> formDataset(List<Goal> goalList) {
        Map<String, Integer> dataset = new HashMap<>();

        dataset.put(GoalState.FINISHED.toString(),
                (int) goalList.stream().filter(x -> x.getState() == GoalState.FINISHED).count());

        dataset.put(GoalState.IN_PROGRESS.toString(),
                (int) goalList.stream().filter(x -> x.getState() == GoalState.IN_PROGRESS).count());

        dataset.put(GoalState.FAILED.toString(),
                (int) goalList.stream().filter(x -> x.getState() == GoalState.FAILED).count());

        return dataset;
    }
}
