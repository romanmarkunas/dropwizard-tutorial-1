package com.romanmarkunas.dwtutorial1;

import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicInteger;

@Path("/time")
@Produces(MediaType.APPLICATION_JSON)
public class TimeResource {

    private final AtomicInteger invocations = new AtomicInteger(0);

    @GET
    public TimeAndInvocations getTimeAndInvocations() {
        int invocation = this.invocations.incrementAndGet();
        return new TimeAndInvocations(LocalTime.now(), invocation);
    }
}
