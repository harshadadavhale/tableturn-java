
package com.tableturn.party;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class PartyIds {

    private final AtomicLong sequence = new AtomicLong(0);

    public String nextId() {
        return "W%03d".formatted(sequence.incrementAndGet());
    }
}
