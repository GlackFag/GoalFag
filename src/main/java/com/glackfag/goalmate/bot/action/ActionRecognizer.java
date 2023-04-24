package com.glackfag.goalmate.bot.action;

import com.glackfag.goalmate.Commands;
import com.glackfag.goalmate.models.Person;
import com.glackfag.goalmate.services.PeopleService;
import com.glackfag.goalmate.util.UpdateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
            if (isShowGoalList(update))
                return Action.SHOW_GOAL_LIST;
            if (isShowGoalDescription(update))
                return Action.SHOW_GOAL_DESCRIPTION;
            if (isFinishGoal(update))
                return Action.FINISH_GOAL;
            if (isFailGoal(update))
                return Action.FAIL_GOAL;
            if (isDeleteGoal(update))
                return Action.DELETE_GOAL;
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

        return Action.NOTHING;
    }

    private boolean isSendGreetings(Update update) {
        Long userId = UpdateUtils.extractUserId(update);
        String userInput = UpdateUtils.extractUserInput(update);

        return userInput.equalsIgnoreCase(Commands.START) && !peopleService.isUserIdRegistered(userId);
    }

    private boolean isRegister(Update update) {
        if (!update.hasCallbackQuery())
            return false;

        Long userId = UpdateUtils.extractUserId(update);
        String callbackDataText = UpdateUtils.extractCallbackDataText(update);

        return callbackDataText.equals(Commands.REGISTER) && !peopleService.isUserIdRegistered(userId);
    }

    private boolean isShowMenu(Update update) {
        String userInput = UpdateUtils.extractUserInput(update);
        long userId = UpdateUtils.extractUserId(update);

        return (userInput.equals(Commands.MENU) || userInput.equalsIgnoreCase(Commands.CANCEL) ||
                userInput.equals(Commands.START)) && !update.hasCallbackQuery() &&
                peopleService.isUserIdRegistered(userId);
    }

    private boolean isShowGoalList(Update update) {
        String callbackDataText = UpdateUtils.extractCallbackDataText(update);

        return callbackDataText.equals(Commands.SHOW_GOAL_LIST);
    }

    private boolean isShowGoalDescription(Update update) {
        String callbackDataText = UpdateUtils.extractCallbackDataText(update);

        return callbackDataText.startsWith(Commands.SHOW_GOAL_DESCRIPTION);
    }

    private boolean isFinishGoal(Update update) {
        String callbackDataText = UpdateUtils.extractCallbackDataText(update);

        return callbackDataText.startsWith(Commands.FINISH_GOAL);
    }

    private boolean isFailGoal(Update update) {
        String callbackDataText = UpdateUtils.extractCallbackDataText(update);

        return callbackDataText.startsWith(Commands.FAIL_GOAL);
    }

    private boolean isDeleteGoal(Update update) {
        String callbackDataText = UpdateUtils.extractCallbackDataText(update);

        return callbackDataText.startsWith(Commands.DELETE_GOAL);
    }

    private boolean isSendNewGoalEssenceForm(Update update) {
        if (!update.hasCallbackQuery())
            return false;

        String callbackDataText = UpdateUtils.extractCallbackDataText(update);

        return callbackDataText.equals(Commands.CREATE_NEW_GOAL);
    }

    private boolean isSendNewGoalTimeframeForm(Update update) {
        String userInput = UpdateUtils.extractUserInput(update);
        Action lastAction = Person.getLastAction(UpdateUtils.extractUserId(update));

        return !update.hasCallbackQuery() && lastAction == Action.SEND_NEW_GOAL_ESSENCE_FORM &&
                !userInput.equalsIgnoreCase(Commands.CANCEL);
    }

    private boolean isSaveGoal(Update update) {
        Long userId = UpdateUtils.extractUserId(update);
        String userInput = UpdateUtils.extractUserInput(update);

        return Person.getLastAction(userId) == Action.SEND_NEW_GOAL_TIMEFRAME_FORM && !userInput.equalsIgnoreCase(Commands.CANCEL);
    }
}
