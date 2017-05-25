package tutorialed;

import peersim.cdsim.CDProtocol;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Linkable;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.transport.Transport;
import peersim.vector.SingleValueHolder;

/**
 * Event driven version of epidemic averaging.
 */
public class AverageED extends SingleValueHolder implements CDProtocol, EDProtocol {

    /**
     * @param prefix string prefix for config properties
     */
    public AverageED(String prefix) { super(prefix); }

    /**
     * This is the standard method the define periodic activity.
     * The frequency of execution of this method is defined by a
     * {@link peersim.edsim.CDScheduler} component in the configuration.
     */
    public void nextCycle(Node node, int pid) {

        Linkable linkable = (Linkable) node.getProtocol( FastConfig.getLinkable(pid) );

        if (linkable.degree() > 0) {

            Node peern = linkable.getNeighbor(CommonState.r.nextInt(linkable.degree()));

            // XXX quick and dirty handling of failures
            // (message would be lost anyway, we save time)
            if(!peern.isUp()) {
                return;
            }

            AverageED peer = (AverageED) peern.getProtocol(pid);

            Transport tr = ((Transport) node.getProtocol(FastConfig.getTransport(pid)));
            tr.send(node, peern, new AverageMessage(value,node), pid);

        }

    }

    /**
     * This is the standard method to define to process incoming messages.
     */
    public void processEvent(Node node, int pid, Object event) {

        AverageMessage aem = (AverageMessage) event;

        if( aem.sender!=null ) {
            Transport tr = (Transport) node.getProtocol(FastConfig.getTransport(pid));
            tr.send(node, aem.sender, new AverageMessage(value,null), pid);
        }

        value = (value + aem.value) / 2;
    }
}
