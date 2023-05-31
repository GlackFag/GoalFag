package com.glackfag.goalfag.statistics;

import com.glackfag.goalfag.models.Goal;
import com.glackfag.goalfag.models.Person;
import com.glackfag.goalfag.models.enums.GoalState;
import com.glackfag.goalfag.services.PeopleService;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class DatasetFormer {
    private final PeopleService peopleService;

    @Autowired
    public DatasetFormer(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @Transactional
    public DefaultPieDataset<String> formAllTimePieDatasetByUserId(long userId) {
        Person person = peopleService.findByUserId(userId);
        List<Goal> goalList = person.getGoals();

        return formDataSet(goalList);
    }

    @Transactional
    public DefaultPieDataset<String> formYearPieDatasetByUserId(long userId) {
        Person person = peopleService.findByUserId(userId);
        List<Goal> goalList = person.getGoals();

        goalList.removeIf(x -> !isGoalForThisYear(x));

        return formDataSet(goalList);
    }

    private static boolean isGoalForThisYear(Goal goal) {
        int currentYear = LocalDate.now().getYear();
        return goal.getCreationDate().getYear() == currentYear &&
                goal.getExpiredDate().getYear() == currentYear;
    }

    public DefaultPieDataset<String> formDataSet(List<Goal> goalList) {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

        dataset.setValue(GoalState.FINISHED.toString(),
                goalList.stream().filter(x -> x.getState() == GoalState.FINISHED).count());

        dataset.setValue(GoalState.IN_PROGRESS.toString(),
                goalList.stream().filter(x -> x.getState() == GoalState.IN_PROGRESS).count());

        dataset.setValue(GoalState.FINISHED.toString(),
                goalList.stream().filter(x -> x.getState() == GoalState.FINISHED).count());

        return dataset;
    }
}
