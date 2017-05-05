package tutorial2.observers;

import peersim.config.Configuration;
import peersim.core.Node;
import peersim.core.Protocol;
import peersim.graph.Graph;
import peersim.reports.GraphObserver;
import peersim.util.FileNameGenerator;
import tutorial2.NewscastProtocol;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class PartialViewObserver extends GraphObserver {


    /**
     * The filename base to print out the topology relations.
     *
     * @config
     */
    private static final String PAR_FILENAME_BASE = "file_base";

    /**
     * The filename base to print out the topology relations.
     *
     * @config
     */
    private static final String PAR_PROT = "protocol";


    /**
     * Topology filename. Obtained from config property
     * {@link #PAR_FILENAME_BASE}.
     */
    private final String graph_filename;

    /**
     * The protocol identifier
     * {@link #PAR_PROT}.
     */
    private static int protocolId;

    /**
     * Utility class to generate incremental indexed filenames from a common
     * base given by {@link #graph_filename}.
     */
    private final FileNameGenerator fng;

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------
    /**
     * Standard constructor that reads the configuration parameters. Invoked by
     * the simulation engine.
     *
     * @param prefix
     *            the configuration prefix for this class.
     */
    public PartialViewObserver(String prefix) {

        super(prefix);
        graph_filename = Configuration.getString(prefix + "." + PAR_FILENAME_BASE, "graph_dump");
        protocolId = Configuration.getPid(prefix + "." + PAR_PROT, 0);
        fng = new FileNameGenerator(graph_filename, ".csv");
    }


    public boolean execute() {

        try {

            updateGraph();

            System.out.print(name + ": ");

            // initialize output streams
            String fname = fng.nextCounterName();
            FileOutputStream fos = new FileOutputStream(fname);
            System.out.println("Writing to file " + fname);
            PrintStream pstr = new PrintStream(fos);

            // dump topology:
            graphToFile(g, pstr);

            fos.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    /**
     * Utility method: prints out data to plot the topology using gnuplot a
     * gnuplot style.
     *
     * @param g current graph.
     * @param ps a {@link java.io.PrintStream} object to write to.
     */
    private static void graphToFile(Graph g, PrintStream ps) {

        for (int i = 1; i < g.size(); i++) {

            Node current = (Node) g.getNode(i);

            Protocol p = current.getProtocol(protocolId);

            NewscastProtocol peer = (NewscastProtocol) p;
            ps.println(i + ";" + peer.toString());
        }
    }
}
