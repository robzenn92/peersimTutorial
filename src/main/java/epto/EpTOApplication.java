package epto;

import epto.utils.Event;
import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Node;

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
    private final double prob;

    // =================================
    //  Constructor implementation
    // =================================

    public EpTOApplication(String prefix) {

        PID = Configuration.lookupPid(prefix.replace("protocol.",""));
        prob =  Configuration.getInt(prefix + "." + PAR_PROB);
    }

    public void EpTOBroadcast(Event event, Node node) {

        EpTODissemination disseminationComponent = (EpTODissemination) node.getProtocol(EpTODissemination.PID);
        disseminationComponent.EpTOBroadcast(event, node);
    }

    public void nextCycle(Node node, int protocolID) {

        if (CommonState.r.nextDouble() > prob) {
            System.out.println("Node " + node.getID() + " is EpTOBroadcasting an event at cycle " + CommonState.getTime());
            EpTOBroadcast(new Event(), node);
        }
    }

    public Object clone() {
        return null;
    }


    public void EpTODeliver(Event event, Node node) {
        System.out.println("Node " + node.getID() + " just EpTODelivered event " + event);
    }
}
