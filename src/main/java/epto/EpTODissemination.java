package epto;

import epto.utils.Event;
import epto.utils.Utils;

import newscast.NewscastProtocol;
import newscast.utils.NodeDescriptor;

import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * The algorithm assumes the existence of a peer sampling service (PSS) responsible for keeping p’s view
 * up-to-date with a random stream of at least K deemed correct processes allowing a fanout of size K.
 * TTL (time to live) holds the number of rounds for which each event needs to be relayed during its dissemination.
 * The nextBall set collects the events to be sent in the next round by process p.
 * The dissemination component consists of three procedures executed atomically: the event broadcast primitive,
 * the event receive callback and the periodic relaying task.
 */
public class EpTODissemination implements CDProtocol {

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

    /**
     * The ordering component protocol
     */
    private static final String PAR_PROT_ORD = "protocol_ord";


    // =================================
    //  Parameters
    // =================================

    /**
     * The number of deemed correct processes
     */
    private final int K;

    /**
     * The number of rounds for which each event needs to be relayed during its dissemination
     */
    private final int TTL;

    /**
     * The id of the ordering component protocol
     */
    private final int id_ord_protocol;

    private HashMap<Integer, Event> nextBall = new HashMap<Integer, Event>();

    // =================================
    //  Constructor implementation
    // =================================

    public EpTODissemination(String n) {

        K = Configuration.getInt(n + "." + PAR_FANOUT, Utils.DEFAULT_FANOUT);
        TTL = Configuration.getInt(n + "." + PAR_TTL, Utils.DEFAULT_TTL);
        id_ord_protocol = Configuration.getPid(n + "." + PAR_PROT_ORD);
    }

    /**
     * procedure EpTO-broadcast(event)
     *
     * When p broadcasts an event (lines 6–10), the event is time-stamped with p’s current clock,
     * its ttl is set to zero, and it is added to the nextBall to be relayed in the next round.
     */
    private void EpTO_Broadcast() {

        Event event = new Event();
        event.timestamp = CommonState.getIntTime(); // getClock()
        event.ttl = 0;
        event.sourceId = 1; // TODO: change with this.Node.getID()
        nextBall.put(event.id, event);
    }

    /**
     * upon receive BALL(ball)
     *
     * Upon reception of a ball (lines 11–19), events with ttl < TTL are added to nextBall for further relaying.
     * When a received event is already in nextBall, we keep the one with the largest ttl to avoid excessive retransmissions.
     * Finally, the process clock is updated.
     *
     * @param ball - a ball EpTO broadcasted by another peer
     */
    private void receive(HashMap<Integer, Event> ball) {

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
            // updateClock(event.timestamp)
        }
    }

    public void nextCycle(Node node, int protocolID) {

        int linkableID = FastConfig.getLinkable(protocolID);
        NewscastProtocol linkable = (NewscastProtocol) node.getProtocol(linkableID);

        if (linkable.degree() > 0) {

            for (Event e : nextBall.values()) {
                e.ttl++;
            }

            if (nextBall.size() != 0) {

                ArrayList<NodeDescriptor> peers = linkable.selectNeighbors(K);
                for (NodeDescriptor peer : peers) {
                    send(nextBall, peer.getNode(), protocolID);
                }
            }

            EpTOOrdering EpTOOrdering = (EpTOOrdering) node.getProtocol(id_ord_protocol);
            System.out.println("I know my ordering component is " + EpTOOrdering);
            EpTOOrdering.orderEvents((HashSet<Event>) nextBall.clone());
            nextBall.clear();
        }
    }

    private void send(HashMap<Integer, Event> nextBall, Node peer, int protocolID) {
        EpTODissemination EpTODissemination = (EpTODissemination) peer.getProtocol(protocolID);
        EpTODissemination.receive((HashMap<Integer, Event>) nextBall.clone());
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
