package com.glackfag.goalmate.bot.reminder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Timer;

@Component
public class Reminder {
    @Autowired
    public Reminder(NotifyOrDeletePeopleTask task) {
        Timer timer = new Timer(true);
        timer.schedule(task,  86_400_000L, 86_400_000L);
    }
}
