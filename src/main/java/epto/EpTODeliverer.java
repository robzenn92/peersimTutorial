package epto;

import epto.utils.Event;
import peersim.core.Node;

public interface EpTODeliverer {

    public void EpTODeliver(Event event, Node node);
}
