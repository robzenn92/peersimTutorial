package tutorial;

import peersim.cdsim.CDProtocol;
import peersim.config.FastConfig;
import peersim.core.Linkable;
import peersim.core.Node;
import peersim.vector.SingleValueHolder;
import peersim.config.Configuration;

public class BasicBalance extends SingleValueHolder implements CDProtocol {

    // ------------------------------------------------------------------------
    // Parameters
    // ------------------------------------------------------------------------

    protected static final String PAR_QUOTA = "quota";

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------

    /**
     * Quota amount. Obtained from config property {@link #PAR_QUOTA}.
     */
    private final double quota_value;

    protected double quota; // current cycle quota


    public BasicBalance(String s) {

        super(s);

        // get quota value from the config file. Default 1.
        quota_value = (Configuration.getInt(s + "." + PAR_QUOTA, 1));
        quota = quota_value;
    }

    // Resets the quota.
    protected void resetQuota() {
        this.quota = quota_value;
    }

    public void nextCycle(Node node, int protocolID) {

        int linkableID = FastConfig.getLinkable(protocolID);
        Linkable linkable = (Linkable) node.getProtocol(linkableID);

        if (this.quota == 0) {
            return; // quota is exceeded
        }

        // this takes the most distant neighbor based on local load
        BasicBalance neighbor = null;
        double maxdiff = 0;

        for (int i = 0; i < linkable.degree(); ++i) {

            Node peer = linkable.getNeighbor(i);

            // The selected peer could be inactive
            if (!peer.isUp())
                continue;
            BasicBalance n = (BasicBalance) peer.getProtocol(protocolID);

            if (n.quota == 0.0)
                continue;

            double d = Math.abs(value - n.value);
            if (d > maxdiff) {
                neighbor = n;
                maxdiff = d;
            }
        }
        if (neighbor == null) {
            return;
        }

        doTransfer(neighbor);
    }

    protected void doTransfer(BasicBalance neighbor) {
        double a1 = this.value;
        double a2 = neighbor.value;
        double maxTrans = Math.abs((a1 - a2) / 2);
        double trans = Math.min(maxTrans, quota);
        trans = Math.min(trans, neighbor.quota);

        if (a1 <= a2) {
            a1 += trans;
            a2 -= trans;
        } else {
            a1 -= trans;
            a2 += trans;
        }

        this.value = a1;
        this.quota -= trans;
        neighbor.value = a2;
        neighbor.quota -= trans;
    }
}
