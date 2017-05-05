package unit;

import helpers.NodeHelper;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import peersim.config.Configuration;
import peersim.config.ParsedProperties;
import tutorial2.utils.NodeDescriptor;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class TestNodeDescriptors {


    @BeforeClass
    public void setUpConfiguration() throws IOException {
        Configuration.setConfig(new ParsedProperties("src/test/resources/tests.cfg"));
    }

    @Test
    public void testNewNodeDescriptorRandomAge() {

        NodeDescriptor n = new NodeDescriptor(NodeHelper.createNode(1));
        assertEquals(n.getNode().getID(), 1);
        assertTrue(n.getAge() > 0 && n.getAge() < 20);
    }

    @Test
    public void testNewNodeDescriptorWithAge() {

        NodeDescriptor n = new NodeDescriptor(NodeHelper.createNode(1), 10);
        assertEquals(n.getNode().getID(), 1);
        assertEquals(n.getAge(), 10);
    }

    @Test
    public void testNodeDescriptorsComparision() {


        NodeDescriptor n1 = new NodeDescriptor(NodeHelper.createNode(1), 10);
        NodeDescriptor n2 = new NodeDescriptor(NodeHelper.createNode(2), 10);
        assertEquals(n1.compareTo(n2), 0);

        n1 = new NodeDescriptor(NodeHelper.createNode(1), 5);
        n2 = new NodeDescriptor(NodeHelper.createNode(2), 10);
        assertEquals(n1.compareTo(n2), -1);

        n1 = new NodeDescriptor(NodeHelper.createNode(1), 10);
        n2 = new NodeDescriptor(NodeHelper.createNode(2), 5);
        assertEquals(n1.compareTo(n2), 1);
    }

    @Test
    public void testNodeDescriptorToString() {

        NodeDescriptor n = new NodeDescriptor(NodeHelper.createNode(1), 10);
        assertEquals(n.toString(), "ND: { node = " + 1 + ", age = " + 10 + " }");
    }
}
