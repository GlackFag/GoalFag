package com.glackfag.goalfag.bot.response;

import com.glackfag.goalfag.util.Commands;
import com.glackfag.goalfag.models.Goal;
import com.glackfag.goalfag.services.PeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class MarkupFormer {
    private final PeopleService peopleService;

    @Autowired
    MarkupFormer(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @Transactional
    InlineKeyboardMarkup fromGoalListMarkup(long userId) throws IllegalArgumentException {
        List<Goal> goalList = peopleService.findByUserId(userId).getGoals();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> currentRow = new ArrayList<>();

        int listWidth = 2;

        for (int i = 0; i < goalList.size(); i++) {

            if (i % listWidth == 0) {
                rows.add(currentRow);
                currentRow = new ArrayList<>();
            }

            currentRow.add(makeButtonFromGoal(goalList.get(i)));
        }
        rows.add(currentRow);

        InlineKeyboardButton back = new InlineKeyboardButton("Back");
        back.setCallbackData(Commands.MENU);

        rows.add(List.of(back));

        return new InlineKeyboardMarkup(rows);
    }

    private InlineKeyboardButton makeButtonFromGoal(Goal goal) {
        InlineKeyboardButton button = new InlineKeyboardButton();

        button.setText(goal.getEssence());
        button.setCallbackData(Commands.SHOW_GOAL_DESCRIPTION + goal.getId());

        return button;
    }

    public InlineKeyboardMarkup formEditOptionsMarkup(long goalId) {
        InlineKeyboardButton finish = new InlineKeyboardButton("Finish goal");
        finish.setCallbackData(Commands.FINISH_GOAL + goalId);

        InlineKeyboardButton fail = new InlineKeyboardButton("Fail goal");
        fail.setCallbackData(Commands.FAIL_GOAL + goalId);

        InlineKeyboardButton delete = new InlineKeyboardButton("Delete goal");
        delete.setCallbackData(Commands.DELETE_GOAL + goalId);

        InlineKeyboardButton back = new InlineKeyboardButton("Back");
        back.setCallbackData(Commands.SHOW_GOAL_LIST);

        return new InlineKeyboardMarkup(List.of(List.of(finish), List.of(fail), List.of(delete), List.of(back)));
    }

    public InlineKeyboardMarkup formGoalDescriptionMarkup(long goalId){
        InlineKeyboardButton changeState = new InlineKeyboardButton("Change state");
        changeState.setCallbackData(Commands.SET_EDIT_OPTIONS_MARKUP + goalId);

        InlineKeyboardButton back = new InlineKeyboardButton("Back");
        back.setCallbackData(Commands.SHOW_GOAL_LIST);

        return new InlineKeyboardMarkup(List.of(List.of(changeState), List.of(back)));
    }
}
