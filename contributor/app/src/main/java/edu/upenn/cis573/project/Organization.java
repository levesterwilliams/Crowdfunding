package edu.upenn.cis573.project;

import java.util.LinkedList;
import java.util.List;

public class Organization {

    private String id;
    private String name;
    private List<Fund> funds;

    public Organization(String id, String name) {
        this.id = id;
        this.name = name;
        funds = new LinkedList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Fund> getFunds() {
        return funds;
    }

    public void setFunds(List<Fund> funds) {
        this.funds = funds;
    }
}
