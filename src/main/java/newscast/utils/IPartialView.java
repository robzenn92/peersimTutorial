package newscast.utils;

import peersim.core.Node;
import java.util.List;

public interface IPartialView {

    public boolean addPeer(Node node, boolean avoidLimitCheck);

    public boolean addPeerDescriptor(NodeDescriptor nodeDescriptor, boolean avoidLimitCheck);

    public boolean removePeer(Node node);

    public void removeDuplicates();

    public boolean contains(Node node);

    public boolean contains(NodeDescriptor nodeDescriptor);

    public void sort();

    public NodeDescriptor getPeer(int index);

    public NodeDescriptor getPeerByNodeId(int nodeId);

    public NodeDescriptor getRandomPeer();

    public List<NodeDescriptor> getPeers();

    public List<NodeDescriptor> getPeers(int limit);

    public void clear();

    public int size();
}