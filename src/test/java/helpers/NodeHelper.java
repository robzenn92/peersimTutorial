package helpers;

import peersim.core.Node;
import peersim.core.Protocol;

public class NodeHelper {

    public static Node createNode(final int id) {

        return new Node() {

            public Protocol getProtocol(int i) {
                return null;
            }

            public int protocolSize() {
                return 0;
            }

            public void setIndex(int i) {
            }

            public int getIndex() {
                return 0;
            }

            public long getID() {
                return id;
            }

            public int getFailState() {
                return 0;
            }

            public void setFailState(int i) {

            }

            public boolean isUp() {
                return true;
            }

            public Object clone() {
                return null;
            }
        };
    }
}
