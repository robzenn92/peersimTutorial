package tutorial;

import peersim.cdsim.CDProtocol;
import peersim.core.CommonState;
import peersim.core.Node;

public class PSS implements CDProtocol {

    public PSS(String n) { }

    public void nextCycle(Node node, int i) {
        System.out.println("I am PSS " + node.getID() + " this is cycle " + CommonState.getTime());
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
