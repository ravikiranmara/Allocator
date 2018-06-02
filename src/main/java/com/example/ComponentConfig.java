package com.example;

import java.util.List;

/**
 * Created by wiz on 5/28/18.
 */
public class ComponentConfig {
    private String name;

    private String parent;

    private int resourceAllocated;

    private int cpuUsed;

    private long input;

    private long output;

    private List<String, double> children;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public int getResourceAllocated() {
        return resourceAllocated;
    }

    public void setResourceAllocated(int resourceAllocated) {
        this.resourceAllocated = resourceAllocated;
    }

    public int getCpuUsed() {
        return cpuUsed;
    }

    public void setCpuUsed(int cpuUsed) {
        this.cpuUsed = cpuUsed;
    }

    public long getInput() {
        return input;
    }

    public void setInput(long input) {
        this.input = input;
    }

    public long getOutput() {
        return output;
    }

    public void setOutput(long output) {
        this.output = output;
    }

    public List<String, double> getChildren() {
        return children;
    }

    public void setChildren(List<String, double> children) {
        this.children = children;
    }
}
