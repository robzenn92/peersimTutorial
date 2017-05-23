package newscast;

import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.core.Linkable;
import peersim.core.Node;

import newscast.utils.NodeDescriptor;
import newscast.utils.PartialView;
import newscast.utils.PeerSamplingProtocol;
import newscast.utils.Utils;

import java.util.ArrayList;

public class NewscastProtocol extends PeerSamplingProtocol implements CDProtocol, Linkable {


    // =================================
    //  Parameters
    // =================================

    /**
     * Config parameter name for the cache size
     * @config
     */
    private static final String PAR_CACHE = "cache";

    /**
     * Partial view size indicates how many neighbours are allowed to be in the peer's partial view.
     */
    private int cache;

    /**
     * Partial view containing the peer's neighbours.
     * Its size is the same as the cache size.
     */
    private PartialView view;


    // =================================
    //  Constructor implementation
    // =================================

    public NewscastProtocol(String n) {
        cache = Configuration.getInt(n + "." + PAR_CACHE, Utils.DEFAULT_CACHE_SIZE);
        view = new PartialView(cache);
    }

    // =================================
    //  Newscast implementation
    // =================================

    public NodeDescriptor selectNeighbor() {
        return view.getRandomPeer();
    }

    public ArrayList<NodeDescriptor> selectNeighbors(int k) {

        ArrayList<NodeDescriptor> neighbors = new ArrayList<NodeDescriptor>(k);
        do {
            neighbors.add(selectNeighbor());
        }
        while (neighbors.size() != k);
        return neighbors;
    }

    // =================================
    //  CDProtocol implementation
    // =================================

    public void nextCycle(Node node, int protocolID) {

        // Get a random peer from the PartialView
        NodeDescriptor neighbour = selectNeighbor();
        NewscastProtocol destination = (NewscastProtocol) neighbour.getNode().getProtocol(protocolID);

        // Merge everything
        PartialView cloned = null;
        try {
            cloned = view.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        view.merge(destination.getView(), node, neighbour.getNode());
        // destination.getView().merge(cloned, neighbour.getNode(), node);
    }

    // =================================
    //  Linkable implementation
    // =================================

    public int degree() {
        return view.size();
    }

    public Node getNeighbor(int i) {
        return view.getPeer(i).getNode();
    }

    public boolean addNeighbor(Node node) {
        if ( view.contains(node)) return false;
        return view.addPeer(node,false);
    }

    public boolean contains(Node node) {
        return view.contains(node);
    }

    public void pack() { }

    public void onKill() {
        view = null;
    }

    public Object clone() {

        NewscastProtocol newscast = null;
        try { newscast = (NewscastProtocol) super.clone(); }
        catch( CloneNotSupportedException e ) {} // never happens

        newscast.setCache(cache);
        newscast.setView(new PartialView(cache));

        return newscast;
    }

    public void setCache(int cache) {
        this.cache = cache;
    }

    public PartialView getView() {
        return view;
    }

    public void setView(PartialView view) {
        this.view = view;
    }

}