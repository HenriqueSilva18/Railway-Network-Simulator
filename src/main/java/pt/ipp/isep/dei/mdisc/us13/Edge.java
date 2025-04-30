package pt.ipp.isep.dei.mdisc.us13;

public class Edge {
    public String from;
    public String to;
    public boolean electrified;
    public double distance;

    public Edge(String from, String to, boolean electrified, double distance) {
        this.from = from;
        this.to = to;
        this.electrified = electrified;
        this.distance = distance;
    }
}
