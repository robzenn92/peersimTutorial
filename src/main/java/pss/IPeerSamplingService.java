package pss;

import peersim.core.Node;

import java.util.List;

public interface IPeerSamplingService {

    public Node selectNeighbor();

    public List<Node> selectNeighbors(int k);

    public int degree();

    public void myTurn(Node node, int pid);

}
