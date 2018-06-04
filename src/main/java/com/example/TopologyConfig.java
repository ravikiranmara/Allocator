package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


/**
 * Created by wiz on 5/28/18.
 */
public class TopologyConfig {
    private String topologyName;
    private int cpuPerUnits;
    private int numUnits;
    private List<ComponentConfig> components;

    // constructor
    public TopologyConfig() {
        components = new ArrayList<ComponentConfig>();
    }

    // dump
    public void dump () {
        System.out.println("=====================================================================");
        System.out.println("TopologyConfig");
        System.out.println("-----------------------");
        System.out.println("Name : " + this.getTopologyName());
        System.out.println("Cpu per unit : " + this.getCpuPerUnits());

        System.out.println("Components : ");
        for (ComponentConfig comp : this.getComponents()) {
            comp.dump();
        }
    }

    // getter and setter
    public String getTopologyName() {
        return topologyName;
    }

    public void setTopologyName(String topologyName) {
        this.topologyName = topologyName;
    }

    public int getNumUnits() {
        return numUnits;
    }

    public void setNumUnits(int numUnits) {
        this.numUnits = numUnits;
    }

    public List<ComponentConfig> getComponents() {
        return components;
    }

    public void setComponents(List<ComponentConfig> components) {
        this.components = components;
    }

    public void addComponent(ComponentConfig component) {
        if(null != component)
            components.add(component);
    }

    public int getCpuPerUnits() {
        return cpuPerUnits;
    }

    public void setCpuPerUnits(int cpuPerUnits) {
        this.cpuPerUnits = cpuPerUnits;
    }
}
