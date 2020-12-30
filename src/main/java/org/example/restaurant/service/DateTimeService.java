package org.example.restaurant.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DateTimeService {

    private boolean custom = false;
    private LocalDateTime localDateTime = LocalDateTime.now();

    public LocalDateTime getLocalDateTime() {
        return custom ? localDateTime : LocalDateTime.now();
    }

    public void setSystem() {
        custom = false;
    }

    public void setCustom(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
        this.custom = true;
    }
}
