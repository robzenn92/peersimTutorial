package epto;

import epto.utils.Event;
import peersim.core.Node;

public interface EpTOBroadcaster {

    public void EpTOBroadcast(Event event, Node node) throws CloneNotSupportedException;
}
