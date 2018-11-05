package com.romanmarkunas.dwtutorial1;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalTime;

public class TimeAndInvocations {

    private final LocalTime time;
    private final int currentInvocations;

    public TimeAndInvocations(LocalTime time, int currentInvocations) {
        this.time = time;
        this.currentInvocations = currentInvocations;
    }

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "hh:mm:ss")
    public LocalTime getTime() {
        return time;
    }

    @JsonProperty
    public int getCurrentInvocations() {
        return currentInvocations;
    }
}
