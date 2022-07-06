package edu.upenn.cis573.project;

public class Fund {

    private String id;
    private String name;
    private long target;
    private long totalDonations;

    public Fund(String id, String name, long target, long totalDonations) {
        this.id = id;
        this.name = name;
        this.target = target;
        this.totalDonations = totalDonations;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getTarget() {
        return target;
    }

    public long getTotalDonations() {
        return totalDonations;
    }
}

