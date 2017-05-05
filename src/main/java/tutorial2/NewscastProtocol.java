package tutorial2;

import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.core.Linkable;
import peersim.core.Node;
import tutorial2.utils.*;

public class NewscastProtocol extends PeerSamplingProtocol implements Linkable, CDProtocol {


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
        this.cache = Configuration.getInt(n + "." + PAR_CACHE, Utils.DEFAULT_CACHE_SIZE);
        this.view = new PartialView(cache);
    }

    // =================================
    //  Newscast implementation
    // =================================

    public NodeDescriptor selectNeighbor() {
        return this.view.getRandomPeer();
    }

    public Message prepareRequest(Node source, Node destination) throws CloneNotSupportedException {

        // Fresh descriptor with age equal to zero
        NodeDescriptor me = new NodeDescriptor(source, 0);
        return new Message(me, destination, MessageType.REQUEST, this.view.clone());
    }

    public void sendRequest(Message request, NewscastProtocol destination) throws CloneNotSupportedException {
        destination.onRequest(request, this);
    }

    // TODO: change this.view in the new Message() with something different according to the protocol
    public Message prepareReply(Node source, Node destination) throws CloneNotSupportedException {

        // Fresh descriptor with age equal to zero
        NodeDescriptor me = new NodeDescriptor(source, 0);
        return new Message(me, destination, MessageType.REPLY, this.view.clone());
    }

    public void sendReply(Message request, NewscastProtocol destination) throws CloneNotSupportedException {
        destination.onRequest(request, this);
    }

    public void merge(PartialView view, NodeDescriptor source, Node destination) {
        this.view.merge(view, source, destination);
    }

    // =================================
    //  CDProtocol implementation
    // =================================

    public void nextCycle(Node node, int protocolID) {

        // The age is updated because this is a new cycle
        this.view.updateAge();

        // Get a random peer from the PartialView
        NodeDescriptor neighbour = this.selectNeighbor();

        // Prepare the request message
        Message request = null;
        try {
            request = this.prepareRequest(node, neighbour.getNode());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        NewscastProtocol destination = (NewscastProtocol) neighbour.getNode().getProtocol(protocolID);

        try {
            this.sendRequest(request, destination);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    public void onRequest(Message message, NewscastProtocol destination) throws CloneNotSupportedException {

        MessageType type = message.getType();

        if (type == MessageType.REQUEST) {

            // MessageType.REQUEST: I need to reply back
            Message reply = this.prepareReply(message.getDestination(), message.getSource().getNode());
            this.sendReply(reply, destination);

            // message.getDestination() merges
            this.merge(message.getData(), message.getSource(), message.getDestination());

        } else {

            // MessageType.REPLY: There is no need to reply back.
            // message.getDestination() merges
            this.merge(message.getData(), message.getSource(), message.getDestination());
        }
    }

    // =================================
    //  Linkable implementation
    // =================================

    public int degree() {
        return this.view.size();
    }

    public Node getNeighbor(int i) {
        return this.view.getPeer(i).getNode();
    }

    public boolean addNeighbor(Node node) {
        if (contains(node)) return false;
        return this.view.addPeer(node,false);
    }

    public boolean contains(Node node) {
        return this.view.contains(node);
    }

    public void pack() { }

    public void onKill() {
        this.view = null;
    }

    public Object clone() {

        NewscastProtocol newscast = null;
        try { newscast = (NewscastProtocol) super.clone(); }
        catch( CloneNotSupportedException e ) {} // never happens

        newscast.cache = this.cache;
        newscast.view = new PartialView(cache);

        return newscast;
    }

    @Override
    public String toString() {

        String s = "";

        if (this.view.size() == 0) {
            s +=  "NULL";
        } else {
            for (NodeDescriptor nd : this.view.getPeers())
                s += nd.getNode().getIndex() + ";";
        }
        return s;
    }
}