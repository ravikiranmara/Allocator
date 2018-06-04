package com.example;

import java.util.ArrayList;
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
    List<String> spout;
    List<String> leaves;
    List<String> congested;
    int currentThroughput;

    public void dump() {
        System.out.println("=====================================================================");
        System.out.println("TopologyConfig");
        System.out.println("-----------------------");
        System.out.println("Name : " + this.getName());
        System.out.println("Cpu per unit : " + this.getCpuPerUnit());

        System.out.println("Components : ");
        for (Map.Entry<String, Component> comp : this.getComponents().entrySet()) {
            comp.getValue().dump();
        }

        System.out.println("Spouts :-");
        for (String comp : this.getSpout()) {
            System.out.println(comp);
        }

        System.out.println("leaves:-");
        for (String comp : this.getLeaves()) {
            System.out.println(comp);
        }

        System.out.println("congested:-");
        for (String comp : this.getCongested()) {
            System.out.println(comp);
        }
    }

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

    public List<String> getSpout() {
        return spout;
    }

    public void setSpout(List<String> spout) {
        this.spout = spout;
    }

    public List<String> getLeaves() {
        return leaves;
    }

    public void setLeaves(List<String> leaves) {
        this.leaves = leaves;
    }

    public List<String> getCongested() {
        return congested;
    }

    public void setCongested(List<String> congested) {
        this.congested = congested;
    }

    public int getCurrentThroughput() {
        return currentThroughput;
    }

    public void setCurrentThroughput(int currentThroughput) {
        this.currentThroughput = currentThroughput;
    }

    public void calculateSpouts() {
        this.spout = new ArrayList<String>();
        for (Map.Entry<String, Component> entry : components.entrySet()) {
            Component comp = entry.getValue();
            System.out.println("comp : " + entry.getKey() + comp.getChildren().size());
            if (comp.getParents().size() == 0) {
                this.spout.add(entry.getKey());
            }
        }
    }

    public void calculateLeaves() {
        this.leaves = new ArrayList<String>();
        for (Map.Entry<String, Component> entry : components.entrySet()) {
            Component comp = entry.getValue();
            if (comp.getChildren().size() == 0) {
                System.out.println("Adding leaf : " + comp.getName() + comp.getChildren().size());
                this.leaves.add(entry.getKey());
            }
        }
        System.out.println("leaves:-" + this.getLeaves().size());
    }

    public void calculateCongested() {
        this.congested = new ArrayList<String>();
        for (Map.Entry<String, Component> entry : components.entrySet()) {
            Component comp = entry.getValue();
            long cpuUsed = comp.current.getCpuUsed();
            long cpuAllocated = comp.current.getAllocated() * comp.getCpuPerUnit();
            if(cpuUsed == cpuAllocated) {
                this.congested.add(entry.getKey());
            }
        }
    }

    public void initialize() {
        components = new HashMap<String, Component>();
        spout = new ArrayList<String>();
        leaves = new ArrayList<String>();
        congested = new ArrayList<String>();
    }

    // constructor
    Topology(TopologyConfig topologyConfig) {
        this.initialize();

        // set name
        this.setName(topologyConfig.getTopologyName());
        this.setCpuPerUnit(topologyConfig.getCpuPerUnits());

        // components
        List<ComponentConfig> componentConfigs = topologyConfig.getComponents();
        for (ComponentConfig compConfig : componentConfigs) {
            Component component = new Component(compConfig);
            this.components.put(compConfig.getName(), component);
        }

        // now initialize supporting data
        this.calculateSpouts();
        this.calculateLeaves();
        this.calculateCongested();
    }
}
