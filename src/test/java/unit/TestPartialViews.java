package unit;

import helpers.NodeHelper;

import helpers.PartialViewHelper;
import org.testng.annotations.Test;

import tutorial2.utils.NodeDescriptor;
import tutorial2.utils.PartialView;

import java.util.ArrayList;

import static org.testng.AssertJUnit.*;
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

        int[] ages = new int[] { 9 ,12 , 16};

        PartialView p1 = new PartialView(3);
        p1.addPeerDescriptor(new NodeDescriptor(NodeHelper.createNode(1), ages[0]),true);
        p1.addPeerDescriptor(new NodeDescriptor(NodeHelper.createNode(2), ages[1]),true);
        p1.addPeerDescriptor(new NodeDescriptor(NodeHelper.createNode(3), ages[2]),true);

        PartialView p2 = p1.clone();
        p2.updateAge();

        for (int i = 0; i < p1.size(); i++ ) {
            assertEquals(p1.getPeer(i).getAge(), ages[i]);
            assertEquals(p2.getPeer(i).getAge(), ages[i] + 1);
        }
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
}