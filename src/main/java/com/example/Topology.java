package com.example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wiz on 6/1/18.
 */
public class Topology {
    String name;
    int cpuPerUnit;
    Map<String, Component> components;
    List<Component> spout;
    List<Component> leafs;
    List<Component> congested;
    int currentThroughput;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCpuPerUnit() {
        return cpuPerUnit;
    }

    public void setCpuPerUnit(int cpuPerUnit) {
        this.cpuPerUnit = cpuPerUnit;
    }

    public Map<String, Component> getComponents() {
        return components;
    }

    public void setComponents(Map<String, Component> components) {
        this.components = components;
    }

    public List<Component> getSpout() {
        return spout;
    }

    public void setSpout(List<Component> spout) {
        this.spout = spout;
    }

    public List<Component> getLeafs() {
        return leafs;
    }

    public void setLeafs(List<Component> leafs) {
        this.leafs = leafs;
    }

    public List<Component> getCongested() {
        return congested;
    }

    public void setCongested(List<Component> congested) {
        this.congested = congested;
    }

    public int getCurrentThroughput() {
        return currentThroughput;
    }

    public void setCurrentThroughput(int currentThroughput) {
        this.currentThroughput = currentThroughput;
    }

    // constructor
    Topology(TopologyConfig topologyConfig) {
        // set name
        this.setName(topologyConfig.getTopologyName());
        this.setCpuPerUnit(topologyConfig.getCpuPerUnits());

        List<Component> components = topologyConfig.getComponents();

    }
}
