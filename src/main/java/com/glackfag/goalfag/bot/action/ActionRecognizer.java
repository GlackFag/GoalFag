package com.glackfag.goalfag.bot.action;

import com.glackfag.goalfag.util.Commands;
import com.glackfag.goalfag.models.Person;
import com.glackfag.goalfag.services.PeopleService;
import com.glackfag.goalfag.util.UpdateUtils;
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
        long userId = UpdateUtils.extractUserId(update);
        String userInput = UpdateUtils.extractUserInput(update);
        String callbackDataText = UpdateUtils.extractCallbackDataText(update);

        if (update.hasCallbackQuery()) {
            if (isRegister(userId, callbackDataText))
                return Action.REGISTER;
            if (isSendNewGoalEssenceForm(callbackDataText))
                return Action.SEND_NEW_GOAL_ESSENCE_FORM;
            if (isShowGoalList(callbackDataText))
                return Action.SHOW_GOAL_LIST;
            if (isShowGoalDescription(callbackDataText))
                return Action.SHOW_GOAL_DESCRIPTION;
            if (isFinishGoal(callbackDataText))
                return Action.FINISH_GOAL;
            if (isFailGoal(callbackDataText))
                return Action.FAIL_GOAL;
            if (isDeleteGoal(callbackDataText))
                return Action.DELETE_GOAL;
            if (isProvideStatistics(userId, callbackDataText))
                return Action.PROVIDE_STATISTICS;
            if(isSetEditOptionsMarkup(callbackDataText))
                return Action.SET_EDIT_OPTIONS_MARKUP;
        } else {
            if (isSendGreetings(userId, userInput))
                return Action.SEND_GREETINGS;
            if (isSendNewGoalTimeframeForm(userId, userInput, callbackDataText))
                return Action.SEND_NEW_GOAL_TIMEFRAME_FORM;
        }
        if (isShowMenu(userId, userInput, callbackDataText))
            return Action.SHOW_MENU;
        if (isSaveGoal(userId, userInput))
            return Action.SAVE_GOAL;

        return Action.NOTHING;
    }

    private boolean isSendGreetings(long userId, String userInput) {
        return userInput.equalsIgnoreCase(Commands.START) && !peopleService.isUserIdRegistered(userId);
    }

    private boolean isRegister(long userId, String callbackDataText) {
        if (callbackDataText.isEmpty())
            return false;

        return callbackDataText.equals(Commands.REGISTER) && !peopleService.isUserIdRegistered(userId);
    }

    private boolean isShowMenu(long userId, String userInput, String callbackDataText) {
        return (userInput.equals(Commands.MENU) || userInput.equalsIgnoreCase(Commands.CANCEL) ||
                userInput.equals(Commands.START) || callbackDataText.equals(Commands.MENU)) &&
                peopleService.isUserIdRegistered(userId);
    }

    private boolean isProvideStatistics(long userId, String callbackDataText) {
        return callbackDataText.equals(Commands.PROVIDE_STATISTICS) && peopleService.isUserIdRegistered(userId);
    }

    private boolean isShowGoalList(String callbackDataText) {
        return callbackDataText.equals(Commands.SHOW_GOAL_LIST);
    }

    private boolean isShowGoalDescription(String callbackDataText) {
        return callbackDataText.startsWith(Commands.SHOW_GOAL_DESCRIPTION);
    }

    private boolean isSetEditOptionsMarkup(String callbackDataText){
        return callbackDataText.startsWith(Commands.SET_EDIT_OPTIONS_MARKUP);
    }

    private boolean isFinishGoal(String callbackDataText) {
        return callbackDataText.startsWith(Commands.FINISH_GOAL);
    }

    private boolean isFailGoal(String callbackDataText) {
        return callbackDataText.startsWith(Commands.FAIL_GOAL);
    }

    private boolean isDeleteGoal(String callbackDataText) {
        return callbackDataText.startsWith(Commands.DELETE_GOAL);
    }

    private boolean isSendNewGoalEssenceForm(String callbackDataText) {
        if (callbackDataText.isEmpty())
            return false;

        return callbackDataText.equals(Commands.CREATE_NEW_GOAL);
    }

    private boolean isSendNewGoalTimeframeForm(long userId, String userInput, String callbackDataText) {
        Action lastAction = Person.getLastAction(userId);

        return callbackDataText.isEmpty() && lastAction == Action.SEND_NEW_GOAL_ESSENCE_FORM &&
                !userInput.equalsIgnoreCase(Commands.CANCEL);
    }

    private boolean isSaveGoal(long userId, String userInput) {
        return Person.getLastAction(userId) == Action.SEND_NEW_GOAL_TIMEFRAME_FORM && !userInput.equalsIgnoreCase(Commands.CANCEL);
    }
}
