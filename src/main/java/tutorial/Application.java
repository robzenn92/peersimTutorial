package tutorial;

import peersim.cdsim.CDProtocol;
import peersim.core.CommonState;
import peersim.core.Node;

public class Application implements CDProtocol {

    public Application(String n) { }

    public void nextCycle(Node node, int i) {
        System.out.println("I am application " + node.getID() + " this is cycle " + CommonState.getTime());
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}