package com.glackfag.goalmate.bot.reminder;

import com.glackfag.goalmate.bot.Bot;
import com.glackfag.goalmate.models.Person;
import com.glackfag.goalmate.services.PeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.TimerTask;

@Component
class NotifyOrDeletePeopleTask extends TimerTask {
    private final PeopleService peopleService;
    private final Bot bot;

    @Value("${message.reminder}")
    private String reminderText;

    @Autowired
    NotifyOrDeletePeopleTask(PeopleService peopleService, Bot bot) {
        this.peopleService = peopleService;
        this.bot = bot;
    }

    @Override
    public void run() {
        List<Person> lastMessageYearAgo = peopleService.findNotWrittenLongTime();

        Date yearAgo = Date.valueOf(LocalDate.now().minusDays(365));

        for (Person e : lastMessageYearAgo) {
            if (e.getLastConverseDate().before(yearAgo))
                peopleService.delete(e);
            else {
                try {
                    bot.execute(new SendMessage(e.getChatId().toString(), reminderText));
                } catch (TelegramApiException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

    }
}