package epto.utils;

import peersim.core.CommonState;

public class Event implements Comparable<Event>{

    public int id;

    public int timestamp;

    public int ttl;

    public int sourceId;

    public Event() {
        id = CommonState.r.nextInt();
    }

    public int compareTo(Event o) {
        return (timestamp - o.timestamp) + (sourceId - o.sourceId);
    }

    @Override
    public String toString() {
        return "{id=" + id + ", timestamp=" + timestamp + ", ttl=" + ttl + ", sourceId=" + sourceId + '}';
    }
}
