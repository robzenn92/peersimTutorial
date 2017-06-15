package epto.utils;

import peersim.core.Node;

public class Message {

    public final static int PREBROADCAST = 0;
    public final static int BROADCAST = 1;
    public final static int DELIVER = 2;
    public final static int DISSEMINATION = 3;
    public final static int ORDER = 4;

    private int type;
    private Object content;
//    private Node source;
//    private Node destination;

    public Message(int type, Object content) {
        this.type = type;
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

//    public Node getSource() {
//        return source;
//    }
//
//    public void setSource(Node source) {
//        this.source = source;
//    }
//
//    public Node getDestination() {
//        return destination;
//    }
//
//    public void setDestination(Node destination) {
//        this.destination = destination;
//    }
}