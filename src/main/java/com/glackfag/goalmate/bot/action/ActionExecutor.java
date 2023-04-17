package com.glackfag.goalmate.bot.action;

import com.glackfag.goalmate.bot.Bot;
import com.glackfag.goalmate.bot.GoalFormer;
import com.glackfag.goalmate.bot.response.ResponseGenerator;
import com.glackfag.goalmate.models.Person;
import com.glackfag.goalmate.services.PeopleService;
import com.glackfag.goalmate.util.UpdateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Component
public class ActionExecutor {

    private final PeopleService peopleService;
    private final ResponseGenerator responseGenerator;
    private final GoalFormer goalFormer;
    private final ApplicationContext applicationContext;
    private Bot bot;

    @Autowired
    public ActionExecutor(PeopleService peopleService, GoalFormer goalFormer,
                          ResponseGenerator responseGenerator, ApplicationContext applicationContext, @Lazy Bot bot) {
        this.peopleService = peopleService;
        this.goalFormer = goalFormer;
        this.responseGenerator = responseGenerator;
        this.applicationContext = applicationContext;
        this.bot = bot;
    }


    public void execute(Action action, Update update) throws Exception {
        Long userId = UpdateUtils.extractUserId(update);
        Action lastAction = Person.getLastAction(userId);

        removeInlineMarkup(update);
        try {
            Person.updateLastAction(userId, action);
            switch (action) {
                case REGISTER -> register(update);
                case SEND_NEW_GOAL_TIMEFRAME_FORM -> sendNewGoalTimeframeForm(update);
                case SAVE_GOAL -> saveGoal(update);

                case SHOW_MENU -> showMenu(update);

                default -> bot.execute(responseGenerator.generate(update, action));
            }
        } catch (Exception e) {
            sendErrorMessage(update);
            Person.updateLastAction(userId, lastAction);

            throw new RuntimeException(e);
        }
    }

    /**
     * removes markup and appends message with text of chosen inline button
     */
    private void removeInlineMarkup(Update update) throws TelegramApiException {
        if (!update.hasCallbackQuery())
            return;

        CallbackQuery callback = update.getCallbackQuery();
        Message messageToEdit = callback.getMessage();

        List<InlineKeyboardMarkup> allMarkups = (List<InlineKeyboardMarkup>) applicationContext.getBean("allMarkups");
        String chosenButtonText = UpdateUtils.getUsedInlineButton(update, allMarkups).getText();

        EditMessageText editMessage = new EditMessageText();

        editMessage.setMessageId(messageToEdit.getMessageId());
        editMessage.setChatId(messageToEdit.getChatId());
        editMessage.setReplyMarkup(null);
        editMessage.setText(messageToEdit.getText() + "\n\n" + chosenButtonText);

        bot.execute(editMessage);
    }

    private void register(Update update) throws TelegramApiException {
        Long userId = UpdateUtils.extractUserId(update);

        Person person = new Person(userId);

        peopleService.save(person);

        bot.execute(responseGenerator.generate(update, Action.SHOW_MENU));
    }


    private void sendNewGoalTimeframeForm(Update update) throws TelegramApiException {
        Long userId = UpdateUtils.extractUserId(update);
        String essence = UpdateUtils.extractUserInput(update);

        goalFormer.formNewGoal(userId, essence);

        bot.execute(responseGenerator.generate(update, Action.SEND_NEW_GOAL_TIMEFRAME_FORM));
    }

    private void saveGoal(Update update) throws Exception {
        Long userId = UpdateUtils.extractUserId(update);

        LocalDate expiredDate;
        if (update.hasCallbackQuery()) {
            int monthsPeriod = Integer.parseInt(UpdateUtils.extractCallbackDataText(update));
            expiredDate = LocalDate.now().plusMonths(monthsPeriod);
        } else {
            String userInput = UpdateUtils.extractUserInput(update).trim();
            if (userInput.equalsIgnoreCase("Cancel"))
                return;

            if (!isDateValid(userInput)) {
                Person.updateLastAction(userId, Action.SEND_NEW_GOAL_TIMEFRAME_FORM);
                execute(Action.SEND_RETRY, update);
                execute(Action.SEND_NEW_GOAL_TIMEFRAME_FORM, update);
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

    private void showMenu(Update update) throws TelegramApiException {
        bot.execute(responseGenerator.generate(update, Action.SHOW_MENU));
    }

    private void sendErrorMessage(Update update) throws TelegramApiException {
        bot.execute(responseGenerator.generate(update, Action.SEND_ERROR_MESSAGE));
        showMenu(update);
    }
}
