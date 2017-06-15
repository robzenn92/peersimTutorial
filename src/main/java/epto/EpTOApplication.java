package epto;

import epto.utils.Event;
import epto.utils.Message;
import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.edsim.EDSimulator;

import java.util.ArrayList;

public class EpTOApplication implements CDProtocol, EDProtocol, EpTOBroadcaster, EpTODeliverer {

    // =================================
    //  Configuration Parameters
    // =================================

    /**
     * Config parameter name for the probability to EpTOBroadcast an event
     * Its value must be between 0 and 1
     * @config
     */
    private static final String PAR_PROB = "prob";

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

        System.out.println(node.getID() + " maybe is broadcasting");
        if (CommonState.r.nextDouble() <= PROB) {
            System.out.println(node.getID() + " is EpTOBroadcasting.." );
            EpTOBroadcast(new Event(), node);
        }
    }

    public void EpTOBroadcast(Event event, Node node) {
        Message m = new Message(Message.PREBROADCAST, event);
        EDSimulator.add(0, m, node, EpTODissemination.PID);
    }

    public Object clone() {
        EpTOApplication epToApplication = null;
        try {
            epToApplication = (EpTOApplication) super.clone();
            epToApplication.delivered = new ArrayList<Event>();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return epToApplication;
    }

    public void EpTODeliver(Event event, Node node) {
        delivered.add(event);
        System.out.println(node.getID() + " delivered " + event);
        System.out.println(node.getID() + " delivered in total " + delivered);
    }

    public void processEvent(Node node, int i, Object o) {
        Message m = (Message) o;
        switch (m.getType()) {
            case Message.DELIVER:
                EpTODeliver((Event) m.getContent(), node);
                break;
        }
    }
}