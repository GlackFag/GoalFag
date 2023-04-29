package com.glackfag.goalmate.statistics;

import com.glackfag.goalmate.models.Goal;
import com.glackfag.goalmate.models.Person;
import com.glackfag.goalmate.models.enums.GoalState;
import com.glackfag.goalmate.services.PeopleService;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
class DatasetFormer {
    private final PeopleService peopleService;

    @Autowired
    public DatasetFormer(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @Transactional
    DefaultPieDataset<String> formPieDatasetByUserId(long userId) {
        Person person = peopleService.findByUserId(userId);
        List<Goal> goalList = person.getGoals();

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
