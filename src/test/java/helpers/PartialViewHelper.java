package helpers;

import tutorial2.utils.NodeDescriptor;
import tutorial2.utils.PartialView;

import java.util.Random;


public class PartialViewHelper {

    public static PartialView generatePartialView(int size) {
        PartialView view = new PartialView(size);
        for(int i = 1; i <= size; i++) {
            view.addPeerDescriptor(new NodeDescriptor(NodeHelper.createNode(i), i), true);
        }
        return view;
    }

    public static PartialView generateRandomPartialView() {
        int size = new Random().nextInt(20);
        PartialView view = new PartialView(size);

        for(int i = 1; i <= size; i++) {
            view.addPeerDescriptor(new NodeDescriptor(NodeHelper.createNode(new Random().nextInt(40)), new Random().nextInt(40)), true);
        }
        return view;
    }
}