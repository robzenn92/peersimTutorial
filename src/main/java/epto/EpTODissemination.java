package epto;

import epto.utils.Ball;
import epto.utils.Event;
import epto.utils.Utils;

import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.transport.Transport;
import pss.IPeerSamplingService;
import time.LogicalClock;

import java.util.List;

/**
 * The EpTO dissemination component
 *
 * The algorithm assumes the existence of a peer sampling service (PSS) responsible for keeping p’s view
 * up-to-date with a random stream of at least K deemed correct processes allowing a fanout of size K.
 * TTL (time to live) holds the number of rounds for which each event needs to be relayed during its dissemination.
 * The nextBall set collects the events to be sent in the next round by process p.
 * The dissemination component consists of three procedures executed atomically: the event broadcast primitive,
 * the event receive callback and the periodic relaying task.
 */
public class EpTODissemination implements CDProtocol, EDProtocol, EpTOBroadcaster {

    // =================================
    //  Configuration Parameters
    // =================================

    /**
     * Config parameter name for the fanout K: number of deemed correct processes
     * @config
     */
    private static final String PAR_FANOUT = "fanout";

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
     * The number of deemed correct processes
     */
    private final int K;

    /**
     * The number of rounds for which each event needs to be relayed during its dissemination
     */
    private final int TTL;

    /**
     * The nextBall set collects the events to be sent in the next round by process p
     * It relies on an HashMap<Integer, Event>
     */
    private Ball nextBall = new Ball();

    /**
     * Last updated LogicalClock
     */
    private LogicalClock lastLogicalClock;

    // =================================
    //  Constructor implementation
    // =================================

    public EpTODissemination(String prefix) {

        PID = Configuration.lookupPid(prefix.replace("protocol.",""));
        K = Configuration.getInt(prefix + "." + PAR_FANOUT, Utils.DEFAULT_FANOUT);
        TTL = Configuration.getInt(prefix + "." + PAR_TTL, Utils.DEFAULT_TTL);
    }

    // =================================
    //  Implementation
    // =================================

    /**
     * procedure EpTO-broadcast(event)
     *
     * When p broadcasts an event (lines 6–10), the event is time-stamped with p’s current clock,
     * its ttl is set to zero, and it is added to the nextBall to be relayed in the next round.
     */
    public void EpTOBroadcast(Event event, Node node) {

        try {
            event.timestamp = getClock(node.getID());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            event.timestamp = new LogicalClock(node.getID());
        }
        event.ttl = 0;
        event.sourceId = node.getID();
//        System.out.println(node.getID() + " is adding " + event + " to " + nextBall.toString());
        nextBall.put(event.id, event); // nextBall = nextBall U (event.id, event)
    }

    /**
     * Task executed every delta time units
     * @param node the node on which this component is run
     * @param protocolID the id of this protocol in the protocol array
     */
    public void nextCycle(Node node, int protocolID) {

        // Getting the Peer Sampling Services used in the experiment
        IPeerSamplingService pss = (IPeerSamplingService) node.getProtocol(FastConfig.getLinkable(protocolID));

        if (pss.degree() > 0 && CommonState.getTime() > 0) {

//            System.out.println("Node " + node.getID() + " is executing EpTODissemination at cycle " + CommonState.getTime());

            // foreach event in nextBall do
            for (Event event : nextBall.values()) {
                // event.ttl event.ttl + 1
                event.ttl++;
            }

            if (nextBall.size() != 0) {

                List<Node> peers = pss.selectNeighbors(K);

                // foreach q in peers do send BALL(nextBall) to q
                for (Node q : peers) {
                    send(nextBall, node, q);
                }
            }

            // TODO: maybe this can be put within the above statement cause if nextBall is empty there is no need to order it
            // orderEvents(nextBall)
            EpTOOrdering orderingComponent = (EpTOOrdering) node.getProtocol(EpTOOrdering.PID);
            orderingComponent.orderEvents((Ball) nextBall.clone(), node);

            // nextBall = 0

//            System.out.println(node.getID() + " nextBall before clear: " + nextBall);
            nextBall.clear();
//            System.out.println(node.getID() + " nextBall after clear: " + nextBall);
        }
    }

    private void send(Ball nextBall, Node source, Node destination) {

        if (destination.isUp()) {
//            System.out.println(source.getID() + " is sending to " + destination.getID() + " the ball: " + nextBall.toString());
            ((Transport) source.getProtocol(FastConfig.getTransport(PID))).send(source, destination, nextBall.clone(), PID);
        }
    }

    /**
     * upon receive BALL(ball)
     *
     * Upon reception of a ball (lines 11–19), events with ttl < TTL are added to nextBall for further relaying.
     * When a received event is already in nextBall, we keep the one with the largest ttl to avoid excessive retransmissions.
     * Finally, the process clock is updated.
     *
     * @param object - a ball EpTO broadcasted by another peer
     */
    public void processEvent(Node node, int pid, Object object) {

        Ball ball = (Ball) object;

        // foreach event in ball do
        for (Event event : ball.values()) {
            // if event.ttl < TTL then
            if (event.ttl < TTL) {
                // if event.id in nextBall then
                if (nextBall.containsKey(event.id)) {
                    // if nextBall[event.id].ttl < event.ttl then
                    Event target = nextBall.get(event.id);
                    if (target.ttl < event.ttl) {
                        // update TTL
                        // nextBall[event.id].ttl = event.ttl
                        target.ttl = event.ttl;
                    }
                } else {
                    // nextBall = nextBall U (event.id, event)
                    nextBall.put(event.id, event);
                }
            }
            // only needed with logical time
            try {
                updateClock(node.getID(), event.timestamp);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
    }

    private LogicalClock getClock(long id) throws CloneNotSupportedException {
        if (lastLogicalClock == null) {
            lastLogicalClock = new LogicalClock(id);
        }
        lastLogicalClock.increment();
        return lastLogicalClock.clone();
    }

    private LogicalClock updateClock(long id, LogicalClock lc) throws CloneNotSupportedException {
        if (lastLogicalClock == null) {
            lastLogicalClock = new LogicalClock(id);
        }
        lastLogicalClock.setEventId(Math.max(lastLogicalClock.getEventId(), lc.getEventId()));
        return lastLogicalClock.clone();
    }

    public Object clone() {

        EpTODissemination dissemination = null;
        try {
            dissemination  = (EpTODissemination) super.clone();
            dissemination.nextBall = new Ball();
            dissemination.lastLogicalClock = null;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return dissemination;
    }
}
