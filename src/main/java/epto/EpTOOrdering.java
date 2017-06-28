package epto;

import epto.utils.Ball;
import epto.utils.Event;
import epto.utils.Message;
import epto.utils.Utils;
import peersim.config.Configuration;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.edsim.EDSimulator;

import java.util.*;

/**
 * The EpTO ordering component
 */
public class EpTOOrdering implements EDProtocol, EpTODeliverer {

    // =================================
    //  Configuration Parameters
    // =================================

    /**
     * Config parameter name for the TTL (time to live).
     * It is a constant holding the number of rounds for which each event needs to be relayed during its dissemination.
     * @config
     */
    private static final String PAR_TTL = "ttl";

    // =================================
    //  Parameters
    // =================================

    /**
     * The id of this protocol in the protocol array
     */
    public static int PID = 0;

    /**
     * The number of rounds for which each event needs to be relayed during its dissemination
     */
    private final int TTL;

    /**
     * A received map of (id, event) pairs with all known but not yet delivered events
     */
    private HashMap<UUID, Event> received = new HashMap<UUID, Event>();

    /**
     * A delivered set with all the events already delivered to the application
     */
    private HashSet<UUID> delivered = new HashSet<UUID>();

    /**
     * The timestamp of the last event delivered
     */
    private int lastDeliveredTimestamp = 0;

    // =================================
    //  Constructor implementation
    // =================================

    public EpTOOrdering(String prefix) {

        PID = Configuration.lookupPid(prefix.replace("protocol.",""));
        TTL = Configuration.getInt(prefix + "." + PAR_TTL, Utils.DEFAULT_TTL);

        received.clear();
        delivered.clear();
    }

    /**
     * Procedure orderEvents is called every round (line 27 of Al- gorithm 1) and its goal is to deliver events
     * to the application (Algorithm 2, line 30) via the deliver function.
     *
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
    public void orderEvents(Ball ball, Node node) {

        System.out.println("Node " + node.getID() + " is ordering events");

        // update TTL of received events
        for (Event event : received.values()) {
            event.ttl++;
        }

        // all the events received in ball are processed
        for (Event event : ball.values()) {

            // An event already delivered or whose timestamp is smaller than the timestamp of the last event delivered
            // (lastDeliveredTs) is discarded
            // Delivering such an event in the former case would violate integrity due to the delivery of a duplicate,
            // and in the latter case would violate total order.
            if (!delivered.contains(event.id) && event.timestamp.getEventId() >= lastDeliveredTimestamp) {
                if (received.containsKey(event.id)) {
                    if (received.get(event.id).ttl < event.ttl) {
                        received.get(event.id).ttl = event.ttl;
                    }
                } else {
                    System.out.println("Node " + node.getID() + " has put in received : " + event);
                    received.put(event.id, event);
                }
            }
        }

        // collect deliverable events and determine smallest timestamp of non deliverable events
        int minQueuedTimestamp = Integer.MAX_VALUE;
        HashMap<UUID, Event> deliverableEvents = new HashMap<UUID, Event>();

        // collect the deliverable events in the deliverableEvents set and calculate the minimum timestamp (minQueuedTs)
        // of all the events that cannot yet be delivered
        for (Event event : received.values()) {
            // an event e becomes deliverable if it is deemed so by the isDeliverable oracle
            if (isDeliverable(event)) {
                System.out.println("Node " + node.getID() + " has marked as deliverable : " + event);
                deliverableEvents.put(event.id, event);
                // TODO: what does minQueuedTimestamp > event.timestamp.getEventId() mean?
            } else if (minQueuedTimestamp > event.timestamp.getEventId()) {
                minQueuedTimestamp = event.timestamp.getEventId();
            }
        }

        // purge from deliverableEvents all the events whose timestamp is greater than minQueuedTs,
        // as they cannot yet be delivered without violating total order.
        HashSet<UUID> nonDeliverableEvents = new HashSet<UUID>();
        for (Event event : deliverableEvents.values()) {
            if (event.timestamp.getEventId() > minQueuedTimestamp) {
                // ignore deliverable events with timestamp greater than all non-deliverable events
                nonDeliverableEvents.add(event.id);
            } else {
                // event can be delivered, remove from received events
                received.remove(event.id);
            }
        }
        for (UUID uuid : nonDeliverableEvents) {
            deliverableEvents.remove(uuid);
        }

        // the events in deliverableEvents are delivered to the application in timestamp order
        ArrayList<Event> sortedDeliverableEvents = sortEvents(deliverableEvents);

        for (Event event : sortedDeliverableEvents) {
            delivered.add(event.id);
            lastDeliveredTimestamp = event.timestamp.getEventId();
            EpTODeliver(event, node);
        }
    }

    private boolean isDeliverable(Event event) {
        return event.ttl > TTL;
    }

    private ArrayList<Event> sortEvents(HashMap<UUID, Event> events) {

        ArrayList<Event> sorted = new ArrayList<Event>(events.size());
        sorted.addAll(events.values());

        Collections.sort(sorted, new Comparator<Event>() {
            public int compare(Event o1, Event o2) {
                return o1.compareTo(o2);
            }
        });

        return sorted;
    }

    public Object clone() {

        EpTOOrdering ordering = null;
        try {
            ordering  = (EpTOOrdering) super.clone();
            ordering.received = new HashMap<UUID, Event>();
            ordering.delivered = new HashSet<UUID>();
            ordering.lastDeliveredTimestamp = 0;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return ordering;
    }

    // TODO: deliver event to the application
    public void EpTODeliver(Event event, Node node) {
        Message m = new Message(Message.DELIVER, event);
        EDSimulator.add(10, m, node, EpTOApplication.PID);
    }

    public void processEvent(Node node, int i, Object o) {

//        Message m = (Message) o;
//        switch (m.getType()) {
//            case Message.ORDER:
//                orderEvents((Ball) m.getContent(), node);
//                break;
//        }
    }
}