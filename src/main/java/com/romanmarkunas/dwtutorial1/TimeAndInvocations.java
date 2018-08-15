package com.romanmarkunas.dwtutorial1;

import java.time.LocalTime;

public class TimeAndInvocations {

    private final LocalTime time;
    private final int currentInvocations;

    public TimeAndInvocations(LocalTime time, int currentInvocations) {
        this.time = time;
        this.currentInvocations = currentInvocations;
    }

    @JsonProperty
    public LocalTime getTime() {
        return time;
    }

    @JsonProperty
    public int getCurrentInvocations() {
        return currentInvocations;
    }
}
