package com.glackfag.goalfag;

import com.glackfag.goalfag.util.Commands;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;


@SpringBootApplication
@EnableTransactionManagement
@PropertySources({
        @PropertySource("classpath:application.properties"),
        @PropertySource("classpath:messages.properties")})
@Slf4j
public class GoalMateApplication {
    private final Environment environment;

    @Autowired
    public GoalMateApplication(Environment environment) {
        this.environment = environment;
    }

    public static void main(String[] args) {
        SpringApplication.run(GoalMateApplication.class, args);
    }

    @Bean
    public String botUsername() {
        return environment.getRequiredProperty("bot.username");
    }

    @Bean
    public String token() {
        return environment.getRequiredProperty("bot.token");
    }

    @Bean
    public String reminderText(){
        return environment.getRequiredProperty("message.reminder");
    }

    @Bean
    public TelegramBotsApi telegramBotsApi() {
        try {
            return new TelegramBotsApi(DefaultBotSession.class);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
            throw new Error(e);
        }
    }

    @Bean
    public Map<String, String> messages() {
        Map<String, String> messages = new HashMap<>();
        Properties properties = new Properties();

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("messages.properties")) {
            properties.load(inputStream);
        } catch (IOException | NullPointerException e) {
            log.error("Error: Unable to read messages from messages.properties");
            throw new Error(e);
        }

        for (var e : properties.entrySet()) {
            String key = (String) e.getKey();
            key = key.replaceFirst("\\w+\\.", "");

            messages.put(key, (String) e.getValue());
        }
        return Collections.unmodifiableMap(messages);
    }

    @Bean
    public InlineKeyboardMarkup registerMarkup() {
        InlineKeyboardButton button = new InlineKeyboardButton("Start!");
        button.setCallbackData(Commands.REGISTER);

        return new InlineKeyboardMarkup(List.of(List.of(button)));
    }

    @Bean
    public InlineKeyboardMarkup menuMarkup() {
        InlineKeyboardButton newGoal = new InlineKeyboardButton("New goal");
        newGoal.setCallbackData(Commands.CREATE_NEW_GOAL);

        InlineKeyboardButton editGoal = new InlineKeyboardButton("See goals");
        editGoal.setCallbackData(Commands.SHOW_GOAL_LIST);

        InlineKeyboardButton provideStatistics = new InlineKeyboardButton("See statistics");
        provideStatistics.setCallbackData(Commands.PROVIDE_STATISTICS);

        return new InlineKeyboardMarkup(List.of(List.of(newGoal), List.of(editGoal), List.of(provideStatistics)));
    }

    @Bean
    public InlineKeyboardMarkup timeframeInstructionMarkup() {
        InlineKeyboardButton year = new InlineKeyboardButton("Year");
        year.setCallbackData("12");

        InlineKeyboardButton halfOfYear = new InlineKeyboardButton("6 months");
        halfOfYear.setCallbackData("6");

        return new InlineKeyboardMarkup(List.of(List.of(year), List.of(halfOfYear)));
    }
}
