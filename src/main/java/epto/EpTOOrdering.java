package epto;

import epto.utils.Ball;
import epto.utils.Event;
import epto.utils.Utils;
import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.core.Node;

import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * The EpTO ordering component
 */
public class EpTOOrdering implements CDProtocol, EpTODeliverer {

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

    public EpTOOrdering(String prefix) {

        PID = Configuration.lookupPid(prefix.replace("protocol.",""));
        TTL = Configuration.getInt(prefix + "." + PAR_TTL, Utils.DEFAULT_TTL);

        received.clear();
        delivered.clear();
    }

    public void nextCycle(Node node, int protocolID) {

//        System.out.println("Node " + node.getID() + " is executing EpTOOrdering at cycle " + CommonState.getTime());
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

        // TODO: handle when ball is null
//        System.out.println(node.getID() + " is ordering events");
//        System.out.println(node.getID() + " ball: " + ball);
//        System.out.println(node.getID() + " received: " + received);
//        System.out.println(node.getID() + " delivered: " + delivered);

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
                    received.put(event.id, event);
                }
            }
        }

//        System.out.println("----");
//        System.out.println(node.getID() + " received: " + received);

        // collect deliverable events and determine smallest timestamp of non deliverable events
        int minQueuedTimestamp = Integer.MAX_VALUE;
        HashMap<Integer, Event> deliverableEvents = new HashMap<Integer, Event>();

        // collect the deliverable events in the deliverableEvents set and calculate the minimum timestamp (minQueuedTs)
        // of all the events that cannot yet be delivered
        for (Event event : received.values()) {
            // an event e becomes deliverable if it is deemed so by the isDeliverable oracle
            if (isDeliverable(event)) {
                deliverableEvents.put(event.id, event);
                // TODO: what does minQueuedTimestamp > event.timestamp.getEventId() mean?
            } else if (minQueuedTimestamp > event.timestamp.getEventId()) {
                minQueuedTimestamp = event.timestamp.getEventId();
            }
        }

        // purge from deliverableEvents all the events whose timestamp is greater than minQueuedTs,
        // as they cannot yet be delivered without violating total order.
        for (Event event : deliverableEvents.values()) {
            if (event.timestamp.getEventId() > minQueuedTimestamp) {
                // ignore deliverable events with timestamp greater than all non-deliverable events
                deliverableEvents.remove(event.id);
            } else {
                // event can be delivered, remove from received events
                received.remove(event.id);
            }
        }

//        System.out.println(node.getID() + " deliverableEvents: " + deliverableEvents);
//        System.out.println("----");

        // the events in deliverableEvents are delivered to the application in timestamp order
        ArrayList<Event> sortedDeliverableEvents = sortEvents(deliverableEvents);

//        System.out.println(node.getID() + " sortedDeliverableEvents: " + sortedDeliverableEvents.toString());
//        System.out.println("----");

        for (Event event : sortedDeliverableEvents) {
            delivered.add(event.id);
            lastDeliveredTimestamp = event.timestamp.getEventId();
            EpTODeliver(event, node);
        }
    }

    private boolean isDeliverable(Event event) {
        return event.ttl > TTL;
    }

    private ArrayList<Event> sortEvents(HashMap<Integer, Event> events) {

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
            ordering.received = new HashMap<Integer, Event>();
            ordering.delivered = new HashSet<Integer>();
            ordering.lastDeliveredTimestamp = 0;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return ordering;
    }

    // TODO: deliver event to the application
    public void EpTODeliver(Event event, Node node) {
        EpTOApplication application = (EpTOApplication) node.getProtocol(EpTOApplication.PID);
        application.EpTODeliver(event, node);
    }
}
