package com.glackfag.goalmate.bot.action;

import com.glackfag.goalmate.models.Person;
import com.glackfag.goalmate.services.PeopleService;
import com.glackfag.goalmate.util.UpdateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class ActionRecognizer {
    private final PeopleService peopleService;

    @Autowired
    public ActionRecognizer(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    public Action recognize(Update update) {
        if (update.hasCallbackQuery()) {
            if (isRegister(update))
                return Action.REGISTER;
            if (isSendNewGoalEssenceForm(update))
                return Action.SEND_NEW_GOAL_ESSENCE_FORM;
        } else {
            if (isShowMenu(update))
                return Action.SHOW_MENU;
            if (isSendGreetings(update))
                return Action.SEND_GREETINGS;
            if (isSendNewGoalTimeframeForm(update))
                return Action.SEND_NEW_GOAL_TIMEFRAME_FORM;
        }
        if (isSaveGoal(update))
            return Action.SAVE_GOAL;

        return Action.SHOW_MENU;
    }

    private boolean isSendGreetings(Update update) {
        Long userId = UpdateUtils.extractUserId(update);
        Message message = update.getMessage();

        if (!peopleService.isUserIdRegistered(userId))
            return message.getText().equalsIgnoreCase("/start");

        return false;
    }


    private boolean isRegister(Update update) {
        if (!update.hasCallbackQuery())
            return false;

        Long userId = UpdateUtils.extractUserId(update);
        String text = UpdateUtils.extractCallbackDataText(update);

        return text.equals("/register") && !peopleService.isUserIdRegistered(userId);
    }

    private boolean isShowMenu(Update update) {
        return !update.hasCallbackQuery() && UpdateUtils.extractUserInput(update).equals("/menu");
    }

    private boolean isSendNewGoalEssenceForm(Update update) {
        if (!update.hasCallbackQuery())
            return false;

        String text = UpdateUtils.extractCallbackDataText(update);

        return text.equals("/createNewGoal");
    }

    private boolean isSendNewGoalTimeframeForm(Update update) {
        String userInput = UpdateUtils.extractUserInput(update);
        Action lastAction = Person.getLastAction(UpdateUtils.extractUserId(update));

        return !update.hasCallbackQuery() && lastAction == Action.SEND_NEW_GOAL_ESSENCE_FORM &&
                !userInput.equalsIgnoreCase("cancel");
    }

    private boolean isSaveGoal(Update update) {
        Long userId = UpdateUtils.extractUserId(update);
        String userInput = UpdateUtils.extractUserInput(update);

        return Person.getLastAction(userId) == Action.SEND_NEW_GOAL_TIMEFRAME_FORM && !userInput.equalsIgnoreCase("cancel");
    }
}
