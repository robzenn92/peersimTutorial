package epto;

import epto.utils.Event;
import peersim.cdsim.CDProtocol;
import peersim.core.Node;

import java.util.HashMap;
import java.util.HashSet;

/**
 * The EpTO ordering component
 */
public class EpTOOrdering implements CDProtocol {

    /**
     * A received map of (id, event) pairs with all known but not yet delivered events
     */
    private HashMap<Integer, Event> received = new HashMap<Integer, Event>();

    /**
     * A delivered set with all the events already delivered to the application
     */
    private HashSet<Integer> delivered = new HashSet<Integer>();

    /**
     * The timestamp of the last event delivered
     */
    private int lastDeliveredTimestamp = 0;

    // =================================
    //  Constructor implementation
    // =================================

    public EpTOOrdering(String n) {
        received.clear();
        delivered.clear();
    }

    public void nextCycle(Node node, int protocolID) {

    }

    /**
     * Procedure orderEvents is called every round (line 27 of Al- gorithm 1) and its goal is to deliver events
     * to the application (Algorithm 2, line 30).
     * To do so, each process p maintains a received map of (id, event) pairs with all known but not yet delivered events
     * and a delivered set with all the events already delivered to the application.
     *
     * The main task of this procedure is to move events from the received set to the delivered set,
     * preserving the total order of the events. This is done in several steps as follows.
     *
     * We start by incrementing the ttl of all events previously received (lines 6–7) to indicate the start of a new round.
     *
     * @param ball - a ball EpTO sent by another peer
     */
    public void orderEvents(HashMap<Integer, Event> ball) {

        // update TTL of received events
        for (Event event : received.values()) {
            event.ttl++;
        }

        for (Event event : ball.values()) {
            if (!delivered.contains(event.id) || event.timestamp >= lastDeliveredTimestamp) {
                if (received.containsKey(event.id)) {
                    if (received.get(event.id).ttl < event.ttl) {
                        received.get(event.id).ttl = event.ttl;
                    }
                } else {
                    received.put(event.id, event);
                }
            }
        }

        // collect deliverable events and determine smallest timestamp of non deliverable events
        int minQueuedTimestamp = Integer.MAX_VALUE;
        HashMap<Integer, Event> deliverableEvents = new HashMap<Integer, Event>();

        for (Event event : received.values()) {
            if (isDeliverable(event)) {
                deliverableEvents.put(event.id, event);
            } else if (minQueuedTimestamp > event.timestamp) {
                minQueuedTimestamp = event.timestamp;
            }
        }

        for (Event event : deliverableEvents.values()) {

            if (event.timestamp > minQueuedTimestamp) {
                // ignore deliverable events with timestamp greater than all non-deliverable events
                deliverableEvents.remove(event.id);
            } else {
                // event can be delivered, remove from received events
                received.remove(event.id);
            }
        }

        sortEvents(deliverableEvents);
        for (Event event : deliverableEvents.values()) {
            delivered.add(event.id);
            lastDeliveredTimestamp = event.timestamp;
            deliver(event);
        }
    }

    // TODO: define whenever an event is deliverable
    private boolean isDeliverable(Event event) {
        return true;
    }

    // TODO: sort events first by timestamp and then by sourceId
    private void sortEvents(HashMap<Integer, Event> events) {

    }

    // TODO: deliver event to the application
    private void deliver(Event event) {

    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
