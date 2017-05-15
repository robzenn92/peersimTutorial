package newscast.utils;

import peersim.core.Node;

public class Message {

    private NodeDescriptor source;
    private Node destination;

    private MessageType type;
    private PartialView data;

    public Message(NodeDescriptor source, Node destination, MessageType type, PartialView data) {
        this.source = source;
        this.destination = destination;
        this.type = type;
        this.data = data;
    }

    public NodeDescriptor getSource() {
        return this.source;
    }

    public Node getDestination() {
        return this.destination;
    }

    public MessageType getType() {
        return type;
    }

    public PartialView getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Message { " +
                "source = { " + source +
                "}, destination = {" + destination +
                "}, type = " + type +
                ", data = {" + data +
                "}}";
    }
}
