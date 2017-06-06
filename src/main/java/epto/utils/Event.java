package epto.utils;

import peersim.core.CommonState;
import time.LogicalClock;

public class Event implements Comparable<Event>{

    public int id;

    public LogicalClock timestamp;

    public int ttl;

    public long sourceId;

    public Event() {
        id = CommonState.r.nextInt();
    }

    public int compareTo(Event o) {
        int comparisionVC = timestamp.compareTo(o.timestamp);
        return (comparisionVC != 0)? comparisionVC : (int)(sourceId - o.sourceId);
    }

    @Override
    public String toString() {
        return "{timestamp=" + timestamp + ", ttl=" + ttl + ", sourceId=" + sourceId + "}";
    }
}