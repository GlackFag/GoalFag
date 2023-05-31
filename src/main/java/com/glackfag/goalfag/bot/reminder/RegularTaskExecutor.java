package com.glackfag.goalfag.bot.reminder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Timer;

@Component
public class RegularTaskExecutor {
    @Autowired
    public RegularTaskExecutor(NotifyOrDeletePeopleTask notifyOrDeletePeopleTask,
                               SendYearProgressTask sendYearProgressTask) {
        Timer timer = new Timer(true);
        timer.schedule(notifyOrDeletePeopleTask,  86_400_000L, 86_400_000L);
        timer.schedule(sendYearProgressTask, 43_200_000, 43_200_000);
    }
}
