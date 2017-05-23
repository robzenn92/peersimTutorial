package epto.utils;

import peersim.core.CommonState;

public class Event {

    public int id;

    public int timestamp;

    public int ttl;

    public int sourceId;

    public Event() {
        id = CommonState.r.nextInt();
    }
}
