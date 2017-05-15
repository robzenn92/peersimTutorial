package newscast.utils;

import peersim.core.CommonState;
import peersim.core.Node;

public class NodeDescriptor implements Comparable {

    private Node node;
    private int age;

    public NodeDescriptor(Node node) {
        this.node = node;
        this.age = CommonState.getIntTime();
    }

    public NodeDescriptor(Node node, int age) {
        this.node = node;
        this.age = age;
    }

    public int compareTo(Object o) {
        int age1 = ((NodeDescriptor) o).getAge();
        return (age < age1) ? 1 : ((age == age1) ? 0 : -1);
    }

    public void updateAge() {
        this.age++;
    }

    public Node getNode() {
        return node;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return "ND: { node = " + node.getID() + ", age = " + age + " }";
    }
}
