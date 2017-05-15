package unit;

import helpers.NodeHelper;

import helpers.PartialViewHelper;
import org.testng.annotations.Test;

import peersim.core.CommonState;
import peersim.core.Node;
import newscast.utils.NodeDescriptor;
import newscast.utils.PartialView;

import java.util.ArrayList;

import static org.testng.AssertJUnit.*;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class TestPartialViews {

    @Test
    public void testNewPartialView() {

        PartialView view = PartialViewHelper.generateRandomPartialView();

        assertNotNull(view);
        assertNotNull(view.getPeers());
    }

    @Test
    public void testClearPartialView() {

        PartialView view = PartialViewHelper.generateRandomPartialView();
        view.clear();

        assertEquals(view.size(), 0);
    }

    @Test
    public void testClonablePartialView() throws CloneNotSupportedException {

//        int[] ages = new int[] { 9 ,12 , 16};
//
//        PartialView p1 = new PartialView(3);
//        p1.addPeerDescriptor(new NodeDescriptor(NodeHelper.createNode(1), ages[0]),true);
//        p1.addPeerDescriptor(new NodeDescriptor(NodeHelper.createNode(2), ages[1]),true);
//        p1.addPeerDescriptor(new NodeDescriptor(NodeHelper.createNode(3), ages[2]),true);
//
//        PartialView p2 = p1.clone();
//        p2.getPeers();
//
//        for (int i = 0; i < p1.size(); i++ ) {
//            assertEquals(p1.getPeer(i).getAge(), ages[i]);
//            assertEquals(p2.getPeer(i).getAge(), ages[i] + 1);
//        }
    }

    @Test
    public void testRemoveDuplicatesPartialView() {

        int size = 6;
        int duplicatesCount = 2;

        PartialView view = new PartialView(size);

        // To be added to the PartialView
        ArrayList<NodeDescriptor> nodeDescriptors = new ArrayList<NodeDescriptor>(size);
        nodeDescriptors.add(new NodeDescriptor(NodeHelper.createNode(1), 1));
        nodeDescriptors.add(new NodeDescriptor(NodeHelper.createNode(2), 4)); // Duplicate
        nodeDescriptors.add(new NodeDescriptor(NodeHelper.createNode(3), 3));
        nodeDescriptors.add(new NodeDescriptor(NodeHelper.createNode(1), 7)); // Duplicate
        nodeDescriptors.add(new NodeDescriptor(NodeHelper.createNode(2), 2));
        nodeDescriptors.add(new NodeDescriptor(NodeHelper.createNode(4), 4));

        for (NodeDescriptor nd : nodeDescriptors) {
            view.addPeerDescriptor(nd, true);
        }

        view.removeDuplicates();

        assertEquals(size - duplicatesCount, view.size());

        assertTrue(view.contains(nodeDescriptors.get(0)));
        assertTrue(view.contains(nodeDescriptors.get(2)));
        assertTrue(view.contains(nodeDescriptors.get(4)));
        assertTrue(view.contains(nodeDescriptors.get(5)));

        assertFalse(view.contains(nodeDescriptors.get(1)));
        assertFalse(view.contains(nodeDescriptors.get(3)));

        assertEquals(view.getPeerByNodeId(1).getAge(), 1);
        assertEquals(view.getPeerByNodeId(2).getAge(), 2);
        assertEquals(view.getPeerByNodeId(3).getAge(), 3);
        assertEquals(view.getPeerByNodeId(4).getAge(), 4);
    }

    @Test
    public void testSortPartialView() {

        int size = 6;

        PartialView view = new PartialView(size);

        // To be added to the PartialView
        ArrayList<NodeDescriptor> nodeDescriptors = new ArrayList<NodeDescriptor>(size);
        nodeDescriptors.add(new NodeDescriptor(NodeHelper.createNode(0), 0));
        nodeDescriptors.add(new NodeDescriptor(NodeHelper.createNode(1), 0));
        nodeDescriptors.add(new NodeDescriptor(NodeHelper.createNode(2), 2));
        nodeDescriptors.add(new NodeDescriptor(NodeHelper.createNode(3), 1));
        nodeDescriptors.add(new NodeDescriptor(NodeHelper.createNode(4), 0));
        nodeDescriptors.add(new NodeDescriptor(NodeHelper.createNode(5), 3));

        for (NodeDescriptor nd : nodeDescriptors) {
            view.addPeerDescriptor(nd, true);
        }

        System.out.println(view);

        view.sort();

        System.out.println(view);

        ArrayList<NodeDescriptor> peers = view.getPeers();

        assertEquals(peers.get(0), nodeDescriptors.get(5));
        assertEquals(peers.get(1), nodeDescriptors.get(2));
        assertEquals(peers.get(2), nodeDescriptors.get(3));
        assertEquals(peers.get(3), nodeDescriptors.get(0));
        assertEquals(peers.get(4), nodeDescriptors.get(1));
        assertEquals(peers.get(5), nodeDescriptors.get(4));
    }


    @Test
    public void testSublistAndCleanPartialView() {

        int size = 6;
        int limit = 4;

        // To be added to the PartialView
        ArrayList<NodeDescriptor> nodeDescriptors = new ArrayList<NodeDescriptor>(size);
        nodeDescriptors.add(new NodeDescriptor(NodeHelper.createNode(0), 0));
        nodeDescriptors.add(new NodeDescriptor(NodeHelper.createNode(1), 0));
        nodeDescriptors.add(new NodeDescriptor(NodeHelper.createNode(2), 2));
        nodeDescriptors.add(new NodeDescriptor(NodeHelper.createNode(3), 1));
        nodeDescriptors.add(new NodeDescriptor(NodeHelper.createNode(4), 0));
        nodeDescriptors.add(new NodeDescriptor(NodeHelper.createNode(5), 3));

        nodeDescriptors.subList(limit, size).clear();

        assertEquals(4, nodeDescriptors.size());

        for( int i = 0 ; i < limit; i++) {
            assertEquals(nodeDescriptors.get(i).getNode().getID(), i);
        }
    }


    // @Test
    public void testMergePartialView() {

        int size = 4;

        PartialView view1 = new PartialView(size);
        ArrayList<NodeDescriptor> nodeDescriptors;

        Node node1 = NodeHelper.createNode(2);

        // To be added to the PartialView
        nodeDescriptors = new ArrayList<NodeDescriptor>(size);
        nodeDescriptors.add(new NodeDescriptor(NodeHelper.createNode(5), 0));
        nodeDescriptors.add(new NodeDescriptor(NodeHelper.createNode(6), 0));
        nodeDescriptors.add(new NodeDescriptor(NodeHelper.createNode(7), 0));
        nodeDescriptors.add(new NodeDescriptor(NodeHelper.createNode(4), 0));

        for (NodeDescriptor nd : nodeDescriptors) {
            view1.addPeerDescriptor(nd, true);
        }

        PartialView view2 = new PartialView(size);
        Node node2 = NodeHelper.createNode(5);

        // To be added to the PartialView
        nodeDescriptors = new ArrayList<NodeDescriptor>(size);
        nodeDescriptors.add(new NodeDescriptor(NodeHelper.createNode(9), 0));
        nodeDescriptors.add(new NodeDescriptor(NodeHelper.createNode(7), 0));
        nodeDescriptors.add(new NodeDescriptor(NodeHelper.createNode(4), 0));
        nodeDescriptors.add(new NodeDescriptor(NodeHelper.createNode(2), 0));

        for (NodeDescriptor nd : nodeDescriptors) {
            view2.addPeerDescriptor(nd, true);
        }


        System.out.println("pre merge");
        System.out.println("node view: " + view1);
        System.out.println("peer view: " + view2);

        CommonState.setTime(new Long(3));

        // Merge everything
        view1.merge(view2, node1, node2);

        System.out.println("post merge");
        System.out.println("node view: " + view1);
        System.out.println("peer view: " + view2);

        ArrayList<NodeDescriptor> peers1 = view1.getPeers();
        assertEquals(peers1.size(), size);
        assertEquals(peers1.get(0).getNode().getID(), 5);
        assertEquals(peers1.get(1).getNode().getID(), 6);
        assertEquals(peers1.get(2).getNode().getID(), 7);
        assertEquals(peers1.get(3).getNode().getID(), 4);

        assertEquals(peers1.get(0).getAge(), 1);
        assertEquals(peers1.get(1).getAge(), 0);
        assertEquals(peers1.get(2).getAge(), 0);
        assertEquals(peers1.get(3).getAge(), 0);

        ArrayList<NodeDescriptor> peers2 = view1.getPeers();
        assertEquals(peers2.size(), size);
        assertEquals(peers2.get(0).getNode().getID(), 2);
        assertEquals(peers2.get(1).getNode().getID(), 9);
        assertEquals(peers2.get(2).getNode().getID(), 7);
        assertEquals(peers2.get(3).getNode().getID(), 4);

        assertEquals(peers2.get(0).getAge(), 1);
        assertEquals(peers2.get(1).getAge(), 0);
        assertEquals(peers2.get(2).getAge(), 0);
        assertEquals(peers2.get(3).getAge(), 0);
    }



//    [ND: { node = 5, age = 0 }, ND: { node = 6, age = 0 }, ND: { node = 7, age = 0 }, ND: { node = 4, age = 0 }, ND: { node = 9, age = 0 }, ND: { node = 7, age = 0 }, ND: { node = 4, age = 0 }, ND: { node = 2, age = 0 }]
}