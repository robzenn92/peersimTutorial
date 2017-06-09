package epto;

import epto.utils.Event;
import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Node;

import java.util.ArrayList;

public class EpTOApplication implements CDProtocol, EpTOBroadcaster, EpTODeliverer {

    // =================================
    //  Configuration Parameters
    // =================================

    /**
     * Config parameter name for the probability to EpTOBroadcast an event
     * Its value must be between 0 and 1
     * @config
     */
    private static final String PAR_PROB= "prob";

    // =================================
    //  Parameters
    // =================================

    /**
     * The id of this protocol in the protocol array
     */
    public static int PID = 0;

    /**
     * The probability to EpTOBroadcast an event
     */
    private final double PROB;

    private ArrayList<Event> delivered = new ArrayList<Event>();

    // =================================
    //  Constructor implementation
    // =================================

    public EpTOApplication(String prefix) {
        PID = Configuration.lookupPid(prefix.replace("protocol.",""));
        PROB =  Configuration.getDouble(prefix + "." + PAR_PROB);
    }

    public void nextCycle(Node node, int protocolID) {

        System.out.println(node.getID() + " is executing Application with PROB = " + PROB + " at cycle " + CommonState.getTime());

        for (int i = 0; i < 2; i++) {
            if (CommonState.r.nextDouble() <= PROB) {
                EpTOBroadcast(new Event(), node);
            }
        }
    }

    public void EpTOBroadcast(Event event, Node node) {

        EpTODissemination disseminationComponent = (EpTODissemination) node.getProtocol(EpTODissemination.PID);
        disseminationComponent.EpTOBroadcast(event, node);
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void EpTODeliver(Event event, Node node) {
        System.out.println("Node " + node.getID() + " just EpTODelivered event " + event);
        delivered.add(event);
        System.out.println("Node " + node.getID() + " delivered " + delivered);
    }
}