package com.glackfag.goalmate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.*;


@SpringBootApplication
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
    public TelegramBotsApi telegramBotsApi() {
        try {
            return new TelegramBotsApi(DefaultBotSession.class);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
            throw new Error(e);
        }
    }

    @Bean
    public PasswordEncoder encoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return String.valueOf(rawPassword.hashCode());
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return encode(rawPassword).equals(encodedPassword);
            }
        };
        //todo сделать нормальный энкодер
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setDefaultLocale(Locale.ENGLISH);
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");

        return messageSource;
    }

    @Bean
    public Map<String, String> messages() {
        Map<String, String> messages = new HashMap<>();
        messages.put("greetings", environment.getRequiredProperty("message.greetings"));
        messages.put("forgotten", environment.getRequiredProperty("message.forgotten"));
        messages.put("reminder", environment.getRequiredProperty("message.reminder"));
        messages.put("menu", environment.getRequiredProperty("message.menu"));
        messages.put("newGoalEssenceInstruction", environment.getRequiredProperty("message.newGoalEssenceInstruction"));
        messages.put("newGoalTimeframeInstruction", environment.getRequiredProperty("message.newGoalTimeframeInstruction"));
        messages.put("error", environment.getRequiredProperty("message.error"));
        messages.put("retry", environment.getRequiredProperty("message.retry"));

        return Collections.unmodifiableMap(messages);
    }

    @Bean
    public InlineKeyboardMarkup registerMarkup() {
        InlineKeyboardButton button = new InlineKeyboardButton("Start!");
        button.setCallbackData("/register");

        return new InlineKeyboardMarkup(List.of(List.of(button)));
    }

    @Bean
    public InlineKeyboardMarkup menuMarkup() {
        InlineKeyboardButton newGoal = new InlineKeyboardButton("New goal");
        newGoal.setCallbackData("/createNewGoal");

        InlineKeyboardButton completeGoal = new InlineKeyboardButton("Complete goal");
        completeGoal.setCallbackData("/completeGoal");

        InlineKeyboardButton provideStatistics = new InlineKeyboardButton("See statistics");
        provideStatistics.setCallbackData("/provideStat");

        return new InlineKeyboardMarkup(List.of(List.of(newGoal), List.of(completeGoal), List.of(provideStatistics)));
    }

    @Bean
    public InlineKeyboardMarkup timeframeInstructionMarkup(){
        InlineKeyboardButton year = new InlineKeyboardButton("Year");
        year.setCallbackData("12");

        InlineKeyboardButton halfOfYear = new InlineKeyboardButton("6 months");
        halfOfYear.setCallbackData("6");

        return new InlineKeyboardMarkup(List.of(List.of(year), List.of(halfOfYear)));
    }

    @Bean
    public List<InlineKeyboardMarkup> allMarkups(){
        return List.of(registerMarkup(), menuMarkup(), timeframeInstructionMarkup());
    }
}
