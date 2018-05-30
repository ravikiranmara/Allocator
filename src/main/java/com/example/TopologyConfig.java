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
    private int cpuUnits;
    private int numUnits;
    private ArrayList<ComponentConfig> components;

    public String getTopologyName() {
        return topologyName;
    }

    public void setTopologyName(String topologyName) {
        this.topologyName = topologyName;
    }

    public int getCpuUnits() {
        return cpuUnits;
    }

    public void setCpuUnits(int cpuUnits) {
        this.cpuUnits = cpuUnits;
    }

    public int getNumUnits() {
        return numUnits;
    }

    public void setNumUnits(int numUnits) {
        this.numUnits = numUnits;
    }

    public ArrayList<ComponentConfig> getComponents() {
        return components;
    }

    public void setComponents(ArrayList<ComponentConfig> components) {
        this.components = components;
    }

    public void addComponent(ComponentConfig component) {
        if(null != component)
            components.add(component);
    }
}
