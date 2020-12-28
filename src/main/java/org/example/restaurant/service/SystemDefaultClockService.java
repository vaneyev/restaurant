package org.example.restaurant.service;

import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
public class SystemDefaultClockService implements ClockService{
    @Override
    public Clock getClock() {
        return Clock.systemDefaultZone();
    }
}
