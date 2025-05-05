package pt.ipp.isep.dei.mdisc.util;

public class Station {
    private String name;
    private String type;

    public Station(String name) {
        this.name = name;
        this.type = inferType(name);
    }

    private String inferType(String name) {
        String lower = name.toLowerCase();
        if (lower.contains("depot")) return "depot";
        if (lower.contains("terminal")) return "terminal";
        return "station";
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
