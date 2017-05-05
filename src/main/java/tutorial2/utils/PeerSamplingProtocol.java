package tutorial2.utils;

import peersim.core.Node;
import tutorial2.NewscastProtocol;

public abstract class PeerSamplingProtocol  {

    public abstract NodeDescriptor selectNeighbor();

    public abstract Message prepareRequest(Node source, Node destination) throws CloneNotSupportedException;

    public abstract void sendRequest(Message request, NewscastProtocol destination) throws CloneNotSupportedException;

    public abstract Message prepareReply(Node source, Node destination) throws CloneNotSupportedException;

    public abstract void sendReply(Message request, NewscastProtocol destination) throws CloneNotSupportedException;

    public abstract void merge(PartialView view, NodeDescriptor source, Node destination);

}
