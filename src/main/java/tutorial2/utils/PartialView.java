package tutorial2.utils;

import peersim.core.CommonState;
import peersim.core.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class PartialView implements IPartialView {

    /**
     * The maximum number of nodes allowable in the PartialView.
     */
    private int limit;

    /**
     * Nodes in the PartialView
     */
    private ArrayList<NodeDescriptor> view;

    public PartialView(int c) {
        this.limit = c;
        this.view = new ArrayList<NodeDescriptor>(c);
    }

    public boolean addPeer(Node node, boolean avoidLimitCheck) {
        if (avoidLimitCheck || this.view.size() < limit) {
            return this.view.add(new NodeDescriptor(node));
        }
        return false;
    }

    public boolean addPeerDescriptor(NodeDescriptor nodeDescriptor, boolean avoidLimitCheck) {
        if (avoidLimitCheck || this.view.size() < limit) {
            return this.view.add(nodeDescriptor);
        }
        return false;
    }

    public boolean removePeer(Node node) {
        for(NodeDescriptor nd : this.view) {
            if (nd.getNode().getID() == node.getID()) {
                return this.view.remove(nd);
            }
        }
        return false;
    }

    public void removeDuplicates() {

        NodeDescriptor curr, succ;
        ArrayList<NodeDescriptor> toBeRemoved = new ArrayList<NodeDescriptor>();

        int size = this.size();
        for(int i = 0; i < size - 1; i++) {

            curr = this.view.get(i);
            for(int j = i + 1; j < size; j++) {
                succ = this.view.get(j);
                if (curr.getNode().getID() == succ.getNode().getID()) {
                    if (curr.getAge() < succ.getAge()) {
                        toBeRemoved.add(succ);
                    } else {
                        toBeRemoved.add(curr);
                    }
                }
            }
        }
        this.view.removeAll(toBeRemoved);
    }

    public boolean contains(Node node) {
        for(NodeDescriptor nd : this.view) {
            if (nd.getNode().getID() == node.getID()) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(NodeDescriptor nodeDescriptor) {
        for(NodeDescriptor nd : this.view) {
            if (nd.getNode().getID() == nodeDescriptor.getNode().getID() && nd.getAge() == nodeDescriptor.getAge()) {
                return true;
            }
        }
        return false;
    }

    public void sort() {
        Collections.sort(this.view, new Comparator<NodeDescriptor>() {
            public int compare(NodeDescriptor o1, NodeDescriptor o2) {
                return o1.compareTo(o2);
            }
        });
    }

    public NodeDescriptor getPeer(int index) {
        if (index < this.view.size()) {
            return this.view.get(index);
        }
        throw new IndexOutOfBoundsException();
    }

    public NodeDescriptor getPeerByNodeId(int nodeId) {
        for(NodeDescriptor nd : this.view) {
            if (nd.getNode().getID() == nodeId) {
                return nd;
            }
        }
        return null;
    }

    public NodeDescriptor getRandomPeer() {
        return this.view.get(CommonState.r.nextInt(this.view.size()));
    }

    public ArrayList<NodeDescriptor> getPeers() {
        return this.view;
    }

    public ArrayList<NodeDescriptor> getPeers(int limit) {
        return (ArrayList<NodeDescriptor>) this.view.subList(0, limit);
    }

    public void clear() {
        this.view.clear();
        this.view = null;
    }

    public int size() {
        return (this.view != null)? this.view.size() : 0;
    }

    public void merge(PartialView view, NodeDescriptor source, Node destination) {

        // Merge everything
        this.view.addAll(view.getPeers());

        // System.out.println(destination.getID() + " PRE: " + this.view);

        // Avoid duplicates
        this.removePeer(source.getNode());
        this.removePeer(destination);

        this.addPeerDescriptor(new NodeDescriptor(source.getNode(), 0), true);

        //System.out.println(destination.getID() + " AFTER ADDING FRESH DESCR: " + this.view);

        this.sort();
        this.removeDuplicates();

        // System.out.println(destination.getID() + " AFTER SORT + REMOVING DUPLICATES: " + this.view);

        this.view.removeAll(this.view.subList(limit, this.view.size()));

        // System.out.println(destination.getID() + " POST: " +this.view);
    }

    public void updateAge() {
        for(NodeDescriptor nd : this.view) {
            nd.updateAge();
        }
    }

    @Override
    public String toString() {
        return this.view.toString();
    }

    public PartialView clone() throws CloneNotSupportedException {
        PartialView cloned = new PartialView(this.size());
        for (NodeDescriptor nd : this.view) {
            cloned.addPeerDescriptor(new NodeDescriptor(nd.getNode(), nd.getAge()), false);
        }
        return cloned;
    }
}
