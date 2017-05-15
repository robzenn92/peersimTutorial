package newscast.utils;

import peersim.core.CommonState;
import peersim.core.Node;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

public class PartialView implements IPartialView {

    /**
     * The maximum number of nodes allowable in the PartialView.
     */
    private int limit;

    /**
     * Nodes in the PartialView
     */
    private ArrayList<NodeDescriptor> view;
    private HashSet<Long> identifiers; /* stores nodes' id for performance as contains operations happen very often  */

    public PartialView(int c) {
        limit = c;
        view = new ArrayList<NodeDescriptor>(c);
        identifiers = new HashSet<Long>(c);
    }

    public boolean addPeer(Node node, boolean avoidLimitCheck) {
        if (avoidLimitCheck || size() < limit) {
            if(view.add(new NodeDescriptor(node))) {
                return identifiers.add(node.getID());
            }
        }
        return false;
    }

    public boolean addPeerDescriptor(NodeDescriptor nodeDescriptor, boolean avoidLimitCheck) {
        if (avoidLimitCheck || size() < limit) {
            if(view.add(nodeDescriptor)) {
                return identifiers.add(nodeDescriptor.getNode().getID());
            }
        }
        return false;
    }

    public boolean removePeer(long id) {
        for(NodeDescriptor nd : view) {
            if (nd.getNode().getID() == id) {
                if(view.remove(nd)) {
                    return identifiers.remove(id);
                }
            }
        }
        return false;
    }

    public boolean removePeer(Node node) {
        long wanted = node.getID();
        return removePeer(wanted);
    }

    public void removeDuplicates() {

        long curr_id;
        NodeDescriptor curr, succ;
        ArrayList<NodeDescriptor> toBeRemoved = new ArrayList<NodeDescriptor>();

        int size = size();
        for(int i = 0; i < size - 1; i++) {

            curr = view.get(i);
            curr_id = curr.getNode().getID();

            for(int j = i + 1; j < size; j++) {
                succ = view.get(j);
                if (curr_id == succ.getNode().getID()) {
                    if (curr.getAge() <= succ.getAge()) {
                        toBeRemoved.add(succ);
                    } else {
                        toBeRemoved.add(curr);
                    }
                }
            }
        }
        view.removeAll(toBeRemoved);
    }

    public boolean containsById(int id) {
        return identifiers.contains(id);
    }

    public boolean contains(Node node) {
        return identifiers.contains(node.getID());
    }

    public boolean contains(NodeDescriptor nodeDescriptor) {
        for(NodeDescriptor nd : view) {
            if (nd.getNode().getID() == nodeDescriptor.getNode().getID() && nd.getAge() == nodeDescriptor.getAge()) {
                return true;
            }
        }
        return false;
    }

    public void sort() {
        Collections.sort(view, new Comparator<NodeDescriptor>() {
            public int compare(NodeDescriptor o1, NodeDescriptor o2) {
                return o1.compareTo(o2);
            }
        });
    }

    public NodeDescriptor getPeer(int index) {
        if (index < size()) {
            return view.get(index);
        }
        throw new IndexOutOfBoundsException();
    }

    public NodeDescriptor getPeerByNodeId(int nodeId) {
        for(NodeDescriptor nd : view) {
            if (nd.getNode().getID() == nodeId) {
                return nd;
            }
        }
        return null;
    }

    public NodeDescriptor getRandomPeer() {
        return view.get(CommonState.r.nextInt(size()));
    }

    public ArrayList<NodeDescriptor> getPeers() {
        return view;
    }

    public ArrayList<NodeDescriptor> getPeers(int limit) {
        return (ArrayList<NodeDescriptor>) view.subList(0, limit);
    }

    public void clear() {
        view.clear();
        view = null;
    }

    public int size() {
        return view.size();
    }


    public void merge(PartialView neighbourView, Node currentNode, Node neighbourNode) {

        // Merge everything
        view.addAll(neighbourView.getPeers());

        // Avoid duplicates
        removePeer(currentNode);
        removePeer(neighbourNode);
        addPeer(neighbourNode, true);

        sort();
        removeDuplicates(); /* operates on a sorted list ! */

        int size = size();
        if (limit < size) {
            view.subList(limit, size).clear();
        }
    }

    @Override
    public String toString() {
        return view.toString();
    }

    public PartialView clone() throws CloneNotSupportedException {
        PartialView cloned = new PartialView(size());
        for (NodeDescriptor nd : view) {
            cloned.addPeerDescriptor(new NodeDescriptor(nd.getNode(), nd.getAge()), false);
            cloned.identifiers = (HashSet<Long>) identifiers.clone();
        }
        return cloned;
    }
}
