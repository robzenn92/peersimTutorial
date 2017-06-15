# PeerSim Tutorial

### Introduction to PeerSim

Peer-to-peer (P2P) systems can be extremely large scale (millions of nodes). Nodes in the network join and leave continuously. Experimenting with a protocol in such an environment it no easy task at all.
PeerSim has been developed to cope with these properties and thus to reach extreme scala- bility and to support dynamism [1].

PeerSim source code can be downloaded from [http://peersim.sourceforge.net](http://peersim.sourceforge.net/).

## Setup

Here there are 2 steps you need to follow in order to run an experiment using EpTO-related protocols:

1. `git clone https://github.com/robzenn92/peersimTutorial.git`
2. `cd peersimTutorial && make all`

The configurations written in `src/main/resources/newscast_ed.cfg` will be loaded.

However, other configuration files are available and can be found in `src/main/resources/`.

## Protocols

![EpTO Flow Schema](https://raw.githubusercontent.com/robzenn92/peersimTutorial/master/EpTO-flow.jpg)

### References

[1] PeerSim HOWTO:
    Build a new protocol for the PeerSim 1.0 simulator.
    Gian Paolo Jesi
    
### Resources

Here there are a list of useful links

[1] http://peersim.sourceforge.net/tutorial1/tutorial1.pdf

[2] http://peersim.sourceforge.net/newscast/newscast.pdf

[3] http://peersim.sourceforge.net/tutorialed/tutorialed.pdf

[4] https://www.slideshare.net/SijoEmmanuel/peer-sim-p2p-network-58734298

[5] http://www.cs.unibo.it/bison/deliverables/D12-13.pdf

[6] http://www.uio.no/studier/emner/matnat/ifi/INF5040/h16/assignment-3/
