package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wiz on 5/28/18.
 */
public class ComponentConfig {
    private String name;

    private List<String> parents;

    private long resourceAllocated;

    private long cpuUsed;

    private long input;

    private long output;

    private long cpuPerUnit;

    private long maxInputPerUnit;

    private Map<String, Double> children;

    // constructor
    public ComponentConfig() {
        parents = new ArrayList<String>();
        children = new HashMap<String, Double>();
    }

    // dump
    public void dump () {
        System.out.println("Component");
        System.out.println("-----------------------");
        System.out.println("Name : " + this.name);
        System.out.println("input : " + this.getInput());
        System.out.println("output : " + this.getOutput());
        System.out.println("allocated : " + this.getResourceAllocated());
        System.out.println("cpu used : " + this.getCpuUsed());
        System.out.println("cpuPerUnit : " + this.getCpuPerUnit());
        System.out.println("maxInputPerUnit : " + this.getMaxInputPerUnit());

        System.out.println("Parents :- ");
        for (String parent : this.parents)
            System.out.println("\t" + parent);

        System.out.println("Children :-");
        for (Map.Entry<String, Double> child : this.children.entrySet())
            System.out.println("\t" + child.getKey() + ":" + child.getValue());
    }

    // getter and setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getParents() {
        return parents;
    }

    public void addParent(String parent) {
        this.parents.add(parent);
    }

    public void setParents(List<String> parent) {
        this.parents = parent;
    }

    public long getResourceAllocated() {
        return resourceAllocated;
    }

    public void setResourceAllocated(long resourceAllocated) {
        this.resourceAllocated = resourceAllocated;
    }

    public long getCpuUsed() {
        return cpuUsed;
    }

    public void setCpuUsed(long cpuUsed) {
        this.cpuUsed = cpuUsed;
    }

    public long getInput() {
        return input;
    }

    public void setInput(long input) {
        this.input = input;
    }

    public long getMaxInputPerUnit() {
        return this.maxInputPerUnit;
    }

    public void setMaxInputPerUnit(long maxInputPerUnit) {
        this.maxInputPerUnit = maxInputPerUnit;
    }

    public long getOutput() {
        return output;
    }

    public void setOutput(long output) {
        this.output = output;
    }

    public Map<String, Double> getChildren() {
        return children;
    }

    public void setChildren(Map<String, Double> children) {
        this.children = children;
    }

    public void addChild(String name, Double ratio) {
        children.put(name, ratio);
    }

    public long getCpuPerUnit() {
        return cpuPerUnit;
    }

    public void setCpuPerUnit(long cpuPerUnit) {
        this.cpuPerUnit = cpuPerUnit;
    }
}
