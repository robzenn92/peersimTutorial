package pss;

import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Node;

public abstract class PeerSamplingService implements CDProtocol, IPeerSamplingService {

    // =================================
    //  Configuration Parameters
    // =================================

    /**
     * Config parameter name for the start
     * It defines after how many cycles a PSS protocol starts to execute the myTurn function, meaning
     * in which cycle the first myTurn call is invoked
     * @config
     */
    private static final String PAR_START = "start";

    /**
     * Config parameter name for the delta
     * It defines how often (in cycles) a PSS protocol executes the myTurn function
     * For example, a value of delta equal to 3 means that the current PSS protocol
     * executes myTurn once every 3 cycles
     * @config
     */
    private static final String PAR_DELTA = "delta";

    // =================================
    //  Parameters
    // =================================

    /**
     * Parameter value for {@link PeerSamplingService#PAR_START}
     * The default value is 0 meaning that a PSS protocol starts executing myTurn as soon as the simulation starts.
     * @see PeerSamplingService#PAR_START
     */
    private static int start;

    /**
     * Parameter value for {@link PeerSamplingService#PAR_DELTA}
     * The default value is 1 meaning that a PSS protocol executes myTurn at each cycle.
     * @see PeerSamplingService#PAR_DELTA
     */
    private static int delta;

    // =================================
    //  Constructor implementation
    // =================================

    public PeerSamplingService() { }

    public PeerSamplingService(String prefix) {
        start = Configuration.getInt(prefix + "." + PAR_START, 0);
        delta = Configuration.getInt(prefix + "." + PAR_DELTA, 1);
    }

    public void nextCycle(Node node, int pid) {
        long time = CommonState.getTime();
        if (node.isUp() && time >= start && time % delta == 0) {
            myTurn(node, pid);
        }
    }

    public abstract Object clone();
}
