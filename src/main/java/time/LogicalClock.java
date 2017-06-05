package time;

public class LogicalClock implements Comparable<LogicalClock> {

    private long nodeId;
    private int eventId;

    public LogicalClock(long nodeId) {
        this.nodeId = nodeId;
        this.eventId = 0;
    }

    public LogicalClock(long nodeId, int eventId) {
        this(nodeId);
        this.eventId = eventId;
    }

    public void increment() {
        this.eventId++;
    }

    public long getNodeId() {
        return nodeId;
    }

    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int compareTo(LogicalClock o) {
        return (nodeId == o.nodeId)? eventId - o.eventId : (int) nodeId - (int) o.nodeId;
    }

    @Override
    public String toString() {
        return "{nodeId=" + nodeId + ", eventId=" + eventId + "}";
    }

    @Override
    public LogicalClock clone() throws CloneNotSupportedException {
        return new LogicalClock(nodeId, eventId);
    }
}
