package com.glackfag.goalmate.util;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class AutoDeletingHashMap<K, V> extends HashMap<K, V>{

    private final Timer timer;
    @Setter
    @Getter
    private Long timeout;

    public AutoDeletingHashMap(@NonNull Long timeout) {
        timer = new Timer();
        this.timeout = timeout;
    }

    @Override
    public V put(K key, V value){
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                remove(key);
            }
        }, timeout);

        return super.put(key, value);
    }
}
