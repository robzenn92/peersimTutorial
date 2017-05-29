package newscast;

import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Linkable;
import peersim.core.Node;

import newscast.utils.PartialView;
import newscast.utils.Utils;
import pss.PeerSamplingService;

import java.util.ArrayList;

public class NewscastProtocol extends PeerSamplingService implements CDProtocol, Linkable {


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

    public NewscastProtocol(int cache) {
        this.cache = cache;
        this.view = new PartialView(cache);
    }

    public NewscastProtocol(String n) {
        super(n);
        cache = Configuration.getInt(n + "." + PAR_CACHE, Utils.DEFAULT_CACHE_SIZE);
        view = new PartialView(cache);
    }

    // =================================
    //  Newscast implementation
    // =================================

    public Node selectNeighbor() {
        return view.getRandomPeer().getNode();
    }

    public ArrayList<Node> selectNeighbors(int k) {

        ArrayList<Node> neighbors = new ArrayList<Node>(k);
        do {
            neighbors.add(selectNeighbor());
        }
        while (neighbors.size() != k);
        return neighbors;
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
        return new NewscastProtocol(cache);
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

    public void myTurn(Node node, int pid) {

        System.out.println("Node " + node.getID() + " is executing Newscast at cycle " + CommonState.getTime());

        // Get a random peer from the PartialView
        Node neighbour = selectNeighbor();
        NewscastProtocol destination = (NewscastProtocol) neighbour.getProtocol(pid);

        // Merge everything
        PartialView cloned = null;
        try {
            cloned = view.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        view.merge(destination.getView(), node, neighbour);
        // destination.getView().merge(cloned, neighbour.getNode(), node);
    }
}