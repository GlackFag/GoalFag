package com.glackfag.goalmate.bot.action;

import com.glackfag.goalmate.bot.Bot;
import com.glackfag.goalmate.bot.GoalFormer;
import com.glackfag.goalmate.bot.response.ResponseGenerator;
import com.glackfag.goalmate.bot.response.ResponseSender;
import com.glackfag.goalmate.models.Person;
import com.glackfag.goalmate.services.PeopleService;
import com.glackfag.goalmate.util.UpdateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class ActionExecutor {

    private final PeopleService peopleService;
    private final PasswordEncoder encoder;
    private final ResponseSender responseSender;
    private final GoalFormer goalFormer;
    private final ApplicationContext applicationContext;
    private Bot bot;

    @Autowired
    public ActionExecutor(PeopleService peopleService, PasswordEncoder encoder,GoalFormer goalFormer,
                          ResponseSender responseSender, ApplicationContext applicationContext, @Lazy Bot bot) {
        this.peopleService = peopleService;
        this.encoder = encoder;
        this.goalFormer = goalFormer;
        this.responseSender = responseSender;
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
                case SEND_GREETINGS -> sendGreetings(update);
                case REGISTER -> register(update);
                case SEND_NEW_GOAL_ESSENCE_FORM -> sendNewGoalEssenceForm(update);
                case SEND_NEW_GOAL_TIMEFRAME_FORM -> sendNewGoalTimeframeForm(update);
                case SAVE_GOAL -> saveGoal(update);

                case SHOW_MENU -> showMenu(update);
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

    private void sendGreetings(Update update) throws TelegramApiException {
        responseSender.sendGreetingsMessage(update);
    }

    private void register(Update update) throws TelegramApiException {
        Long userId = UpdateUtils.extractUserId(update);

        String encoded = encoder.encode(userId.toString());
        Person person = new Person(encoded);

        peopleService.save(person);

        responseSender.sendRegisterMessage(update);
    }

    private void sendNewGoalEssenceForm(Update update) throws TelegramApiException {
        responseSender.sendNewGoalEssenceFormMessage(update);
    }

    private void sendNewGoalTimeframeForm(Update update) throws TelegramApiException {
        Long userId = UpdateUtils.extractUserId(update);
        String essence = UpdateUtils.extractUserInput(update);

        goalFormer.formNewGoal(userId, essence);

        responseSender.sendNewGoalTimeframeFormMessage(update);
    }

    private void saveGoal(Update update) throws TelegramApiException {
        Long userId = UpdateUtils.extractUserId(update);

        LocalDate expiredDate;
        if (update.hasCallbackQuery()) {
            int monthsPeriod = Integer.parseInt(UpdateUtils.extractCallbackDataText(update));
            expiredDate = LocalDate.now().plusMonths(monthsPeriod);
        }
        else {
            String userInput = UpdateUtils.extractUserInput(update).trim();
            if(userInput.equalsIgnoreCase("Cancel"))
                return;

            if(!isDateValid(userInput)) {
                Person.updateLastAction(userId, Action.SEND_NEW_GOAL_TIMEFRAME_FORM);
                responseSender.sendRetryMessage(update);
                responseSender.sendNewGoalTimeframeFormMessage(update);
                return;
            }

            userInput = "01." + userInput;
            expiredDate = LocalDate.parse(userInput, DateTimeFormatter.ofPattern("dd.MM.yy"));
        }
        goalFormer.setExpiredDate(userId, expiredDate);
        goalFormer.saveGoal(userId);

        showMenu(update);
    }

    private boolean isDateValid(String date){
        if(!date.matches("\\d{2}\\.\\d{2}"))
            return false;

        String[] monthYear = date.split("\\.");
        int month = Integer.parseInt(monthYear[0], 10);
        int year = Integer.parseInt(monthYear[1], 10);

        LocalDate currentDate = LocalDate.now();
        int currentYearShort = currentDate.getYear() % 100;
        int currentMonth = currentDate.getMonthValue();

        return (month >= 1 && month <= 12) && (year >= currentYearShort) &&
                !(currentMonth == month && currentYearShort == year);
    }

    private void showMenu(Update update) throws TelegramApiException {
        responseSender.sendMenuMessage(update);
    }

    private void sendErrorMessage(Update update) throws TelegramApiException {
        responseSender.sendErrorMessage(update);
        showMenu(update);
    }
}
