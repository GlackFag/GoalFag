package com.glackfag.goalfag.bot.reminder;

import com.glackfag.goalfag.bot.Bot;
import com.glackfag.goalfag.bot.response.ResponseGenerator;
import com.glackfag.goalfag.models.Person;
import com.glackfag.goalfag.services.PeopleService;
import com.glackfag.goalfag.statistics.DatasetFormer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

@Component
@Slf4j
class SendYearProgressTask extends TimerTask {
    private final PeopleService peopleService;
    private final ResponseGenerator responseGenerator;
    private final DatasetFormer datasetFormer;
    private final Bot bot;

    @Autowired
    SendYearProgressTask(PeopleService peopleService, ResponseGenerator responseGenerator, DatasetFormer datasetFormer, Bot bot) {
        this.peopleService = peopleService;
        this.responseGenerator = responseGenerator;
        this.datasetFormer = datasetFormer;
        this.bot = bot;
    }

    @Override
    public void run() {
        if (!isTimeForExecution())
            return;

        List<Person> people = peopleService.findAll();

        for (Person e : people) {
            try {
                Map<String, Integer> yearDataSet = datasetFormer.formYearPieDatasetByUserId(e.getUserId());

                bot.sendPhoto(responseGenerator.generateSendPhotoWithPiePlot(e.getUserId(), yearDataSet));
            } catch (TelegramApiException ex) {
                log.warn(ex.getMessage());
                throw new RuntimeException(ex);
            }
        }
    }

    private boolean isTimeForExecution() {
        return LocalDateTime.now().isAfter(LocalDateTime.of(
                LocalDate.of(LocalDate.now().getYear(), 12, 31),
                LocalTime.of(12, 0, 0)));
    }
}
