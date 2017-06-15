package epto.utils;

import peersim.core.CommonState;
import time.LogicalClock;

import java.util.UUID;

public class Event implements Comparable<Event>{

    public UUID id;

    public LogicalClock timestamp;

    public int ttl;

    public long sourceId;

    public Event() {
        id = UUID.randomUUID();
    }

    public int compareTo(Event o) {
        int comparisionLC = timestamp.compareTo(o.timestamp);
        return (comparisionLC != 0)? comparisionLC : (int)(sourceId - o.sourceId);
    }

    @Override
    public String toString() {
        return "{timestamp=" + timestamp + ", ttl=" + ttl + ", sourceId=" + sourceId + "}";
    }
}