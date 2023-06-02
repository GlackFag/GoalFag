package com.glackfag.goalfag.bot.action;

import com.glackfag.goalfag.bot.response.MarkupFormer;
import com.glackfag.goalfag.statistics.DatasetFormer;
import com.glackfag.goalfag.util.Commands;
import com.glackfag.goalfag.bot.Bot;
import com.glackfag.goalfag.bot.GoalFormer;
import com.glackfag.goalfag.bot.response.MessageEditor;
import com.glackfag.goalfag.bot.response.ResponseGenerator;
import com.glackfag.goalfag.models.Goal;
import com.glackfag.goalfag.models.Person;
import com.glackfag.goalfag.models.enums.GoalState;
import com.glackfag.goalfag.services.GoalsService;
import com.glackfag.goalfag.services.PeopleService;
import com.glackfag.goalfag.util.UpdateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

@Component
@Slf4j
public class ActionExecutor {

    private final PeopleService peopleService;
    private final GoalsService goalsService;
    private final ResponseGenerator responseGenerator;
    private final GoalFormer goalFormer;
    private final MessageEditor messageEditor;
    private final MarkupFormer markupFormer;
    private final DatasetFormer datasetFormer;
    private Bot bot;

    @Autowired
    public ActionExecutor(PeopleService peopleService, GoalsService goalsService, GoalFormer goalFormer,
                          ResponseGenerator responseGenerator, MessageEditor messageEditor, MarkupFormer markupFormer, DatasetFormer datasetFormer, @Lazy Bot bot) {
        this.peopleService = peopleService;
        this.goalsService = goalsService;
        this.goalFormer = goalFormer;
        this.responseGenerator = responseGenerator;
        this.messageEditor = messageEditor;
        this.markupFormer = markupFormer;
        this.datasetFormer = datasetFormer;
        this.bot = bot;
    }


    public void execute(Action action, Update update) throws Exception {
        Long userId = UpdateUtils.extractUserId(update);
        Action lastAction = Person.getLastAction(userId);

        if (action != Action.SET_EDIT_OPTIONS_MARKUP)
            messageEditor.removeInlineMarkup(update);

        try {
            Person.updateLastAction(userId, action);
            switch (action) {
                case NOTHING -> {
                }
                case REGISTER -> register(update);
                case SEND_NEW_GOAL_TIMEFRAME_FORM -> sendNewGoalTimeframeForm(update);
                case SAVE_GOAL -> saveGoal(update);
                case SHOW_GOAL_LIST -> showGoalList(update);
                case SET_EDIT_OPTIONS_MARKUP -> setEditOptionsMarkup(update);
                case FINISH_GOAL -> finishGoal(update);
                case FAIL_GOAL -> failGoal(update);
                case DELETE_GOAL -> deleteGoal(update);
                case PROVIDE_STATISTICS -> provideStatistics(update);

                default -> bot.execute(responseGenerator.generate(update, action));
            }
        } catch (Exception e) {
            sendErrorMessage(update);
            if (lastAction != null)
                Person.updateLastAction(userId, lastAction);

            log.warn(e.getMessage());
            throw new RuntimeException(e);
        }

        if (peopleService.isUserIdRegistered(UpdateUtils.extractUserId(update)))
            peopleService.updateLastConverseDate(userId);
    }

    private void register(Update update) throws TelegramApiException {
        Long userId = UpdateUtils.extractUserId(update);
        Person person = new Person(userId);

        person.setChatId(UpdateUtils.extractChatId(update));

        peopleService.save(person);
        bot.execute(responseGenerator.generate(update, Action.SHOW_MENU));
    }

    private void provideStatistics(Update update) throws TelegramApiException {
        long userId = UpdateUtils.extractUserId(update);
        Map<String, Integer> dataset = datasetFormer.formAllTimePieDatasetByUserId(userId);

        bot.sendPhoto(responseGenerator.generateSendPhotoWithPiePlot(UpdateUtils.extractUserId(update),
                dataset));
        showMenu(update);
    }

    private void showGoalList(Update update) throws TelegramApiException {
        Long userId = UpdateUtils.extractUserId(update);

        if (peopleService.hasGoals(userId))
            bot.execute(responseGenerator.generate(update, Action.SHOW_GOAL_LIST));
        else {
            bot.execute(responseGenerator.generate(update, Action.SHOW_NO_GOALS_MESSAGE));
            showMenu(update);
        }
    }

    private void setEditOptionsMarkup(Update update) throws TelegramApiException {
        String callbackDataText = UpdateUtils.extractCallbackDataText(update);
        long goalId = Long.parseLong(callbackDataText.replaceFirst(Commands.SET_EDIT_OPTIONS_MARKUP, ""));

        messageEditor.changeMarkup(UpdateUtils.extractMessage(update), markupFormer.formEditOptionsMarkup(goalId));
    }

    private void finishGoal(Update update) throws Exception {
        String callbackDataText = UpdateUtils.extractCallbackDataText(update);
        callbackDataText = callbackDataText.replaceFirst(Commands.FINISH_GOAL, "");

        long goalId = Long.parseLong(callbackDataText);

        Goal goal = goalsService.findOne(goalId);
        goal.setState(GoalState.FINISHED);

        goalsService.update(goal);
        execute(Action.SHOW_GOAL_LIST, update);
    }

    private void failGoal(Update update) throws Exception {
        String callbackDataText = UpdateUtils.extractCallbackDataText(update);
        callbackDataText = callbackDataText.replaceFirst(Commands.FAIL_GOAL, "");

        long goalId = Long.parseLong(callbackDataText);

        Goal goal = goalsService.findOne(goalId);
        goal.setState(GoalState.FAILED);

        goalsService.update(goal);
        execute(Action.SHOW_GOAL_LIST, update);
    }

    private void deleteGoal(Update update) throws Exception {
        String callbackDataText = UpdateUtils.extractCallbackDataText(update);
        callbackDataText = callbackDataText.replaceFirst(Commands.DELETE_GOAL, "");

        long goalId = Long.parseLong(callbackDataText);

        goalsService.delete(goalId);
        execute(Action.SHOW_GOAL_LIST, update);
    }

    private void sendErrorMessage(Update update) throws TelegramApiException {
        bot.execute(responseGenerator.generate(update, Action.SEND_ERROR_MESSAGE));

        if (peopleService.isUserIdRegistered(UpdateUtils.extractUserId(update)))
            showMenu(update);
    }

    private void showMenu(Update update) throws TelegramApiException {
        bot.execute(responseGenerator.generate(update, Action.SHOW_MENU));
    }

    private void sendNewGoalTimeframeForm(Update update) throws TelegramApiException {
        Long userId = UpdateUtils.extractUserId(update);
        String essence = UpdateUtils.extractUserInput(update);

        goalFormer.formNewGoal(userId, essence);

        bot.execute(responseGenerator.generate(update, Action.SEND_NEW_GOAL_TIMEFRAME_FORM));
    }

    private void sendRetryTimeframeForm(Update update) throws TelegramApiException {
        bot.execute(responseGenerator.generate(update, Action.SEND_NEW_GOAL_TIMEFRAME_FORM));
    }

    private void saveGoal(Update update) throws Exception {
        String userInput = UpdateUtils.extractUserInput(update).trim();
        Long userId = UpdateUtils.extractUserId(update);
        LocalDate expiredDate;

        if (update.hasCallbackQuery()) {
            int monthsPeriod = Integer.parseInt(UpdateUtils.extractCallbackDataText(update));
            expiredDate = LocalDate.now().plusMonths(monthsPeriod);
        } else {
            if (userInput.equalsIgnoreCase("Cancel"))
                return;

            if (!isDateValid(userInput)) {
                Person.updateLastAction(userId, Action.SEND_NEW_GOAL_TIMEFRAME_FORM);
                bot.execute(responseGenerator.generate(update, Action.SEND_RETRY));
                sendRetryTimeframeForm(update);
                return;
            }

            userInput = "01." + userInput;
            expiredDate = LocalDate.parse(userInput, DateTimeFormatter.ofPattern("dd.MM.yy"));
        }
        goalFormer.setExpiredDate(userId, expiredDate);
        goalFormer.saveGoal(userId);

        showMenu(update);
    }

    private boolean isDateValid(String date) {
        if (!date.matches("\\d{2}\\.\\d{2}"))
            return false;

        try {
            date = "01." + date;
            return LocalDate.now().isBefore(LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yy")));
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
