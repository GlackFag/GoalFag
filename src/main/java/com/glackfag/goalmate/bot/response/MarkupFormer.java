package com.glackfag.goalmate.bot.response;

import com.glackfag.goalmate.util.Commands;
import com.glackfag.goalmate.models.Goal;
import com.glackfag.goalmate.models.Person;
import com.glackfag.goalmate.services.PeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
class MarkupFormer {
    private final PeopleService peopleService;

    @Autowired
    MarkupFormer(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @Transactional
    InlineKeyboardMarkup fromGoalListMarkup(long userId) {
        Person person = peopleService.findByUserId(userId);

        List<Goal> goalList = person.getGoals();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (Goal e : goalList) {
            InlineKeyboardButton button = new InlineKeyboardButton();

            button.setText(e.getEssence());
            button.setCallbackData(Commands.SHOW_GOAL_DESCRIPTION + e.getId());

            rows.add(List.of(button));
        }

        return new InlineKeyboardMarkup(rows);
    }

    InlineKeyboardMarkup formEditOptionsMarkup(long goalId){
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
}
