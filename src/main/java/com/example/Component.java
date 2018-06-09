package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wiz on 6/1/18.
 */
public class Component {
    private String name;
    private long inputPerCpu;
    private Double inputToOutputRatio;
    private boolean congested;
    private List<String> parents;
    private Map<String, Double> children;
    private ComponentState projected;
    private ComponentState current;
    private long cpuPerUnit;

    public void dump () {
        System.out.println();

        System.out.println("Name : " + this.getName());
        System.out.println("cpuPerUnit : " + this.cpuPerUnit);
        System.out.println("inputPerCpu : " + this.getInputPerCpu());
        System.out.println("inputToOutputRatio : " + this.getInputToOutputRatio());

        System.out.println("Parents :- ");

        for (String parent : this.parents)
            System.out.println("\t" + parent);

        System.out.println("Children :-");
        for (Map.Entry<String, Double> child : this.children.entrySet())
            System.out.println("\t" + child.getKey() + ":" + child.getValue());

        System.out.println("Current:-");
        this.current.dump();

        System.out.println("Projected:-");
        this.projected.dump();

        System.out.println();
    }

    // getter and setter
    public long getCpuPerUnit() {
        return cpuPerUnit;
    }

    public void setCpuPerUnit(long cpuPerUnit) {
        this.cpuPerUnit = cpuPerUnit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getInputPerCpu() {
        return inputPerCpu;
    }

    public void setInputPerCpu(long inputPerCpu) {
        this.inputPerCpu = inputPerCpu;
    }

    public Double getInputToOutputRatio() {
        return inputToOutputRatio;
    }

    public void setInputToOutputRatio(Double inputToOutputRatio) {
        this.inputToOutputRatio = inputToOutputRatio;
    }

    public boolean isCongested() {
        if (this.getCurrent().getCpuUsed() ==
            this.getCurrent().getAllocated() * this.getCpuPerUnit())
            return true;

        return false;
    }

    public boolean isCongestedProjected() {
        if (this.getProjected().getCpuUsed() ==
            this.getProjected().getAllocated() * this.getCpuPerUnit())
            return true;

        return false;
    }

    public void setCongested(boolean congested) {
        this.congested = congested;
    }

    public ComponentState getCurrent() {
        return current;
    }

    public void setCurrent(ComponentState current) {
        this.current = current;
    }

    public ComponentState getProjected() {
        return projected;
    }

    public void setProjected(ComponentState projected) {
        this.projected = projected;
    }

    public List<String> getParents() {
        return parents;
    }

    public void setParents(List<String> parents) {
        this.parents = parents;
    }

    public Map<String, Double> getChildren() {
        return children;
    }

    public void setChildren(Map<String, Double> children) {
        this.children = children;
    }

    public Double getChildOutputRatio (String name) {
        return this.getChildren().get(name);
    }

    public long getCpuForInput(long input) {
       return (long) Math.ceil((double)input/this.getInputPerCpu());
    }

    public long getResourcesForInput(long input) {
        long cpu = (long) Math.ceil((double)input/this.getInputPerCpu());
        return (long) Math.ceil(cpu/cpuPerUnit);
    }

    public long getMaxInput(long numResources) {
        return numResources * this.getInputPerCpu() * this.getCpuPerUnit();
    }

    public long getNewAllocated() {
        return this.getProjected().getAllocated() - this.getCurrent().getAllocated();
    }

    private void initialize() {
        this.projected = new ComponentState();
        this.current = new ComponentState();
        this.parents = new ArrayList<String>();
        this.children = new HashMap<String, Double>();
    }

    // constructor
    public Component (ComponentConfig componentConfig) {
        this.initialize();
        this.setName(componentConfig.getName());

        // read current state
        this.setCpuPerUnit(componentConfig.getCpuPerUnit());
        this.current.setIn(componentConfig.getInput());
        this.current.setOut(componentConfig.getOutput());
        this.current.setCpuUsed(componentConfig.getCpuUsed());
        this.current.setAllocated(componentConfig.getResourceAllocated());

        this.projected.setIn(componentConfig.getInput());
        this.projected.setOut(componentConfig.getOutput());
        this.projected.setCpuUsed(componentConfig.getCpuUsed());
        this.projected.setAllocated(componentConfig.getResourceAllocated());

        // calculate related stats
        this.setInputPerCpu((long)Math.ceil(this.current.getIn()/this.current.getCpuUsed()));
        double iorat = (double)(this.current.getOut())/this.current.getIn();
        this.setInputToOutputRatio(iorat);
        if (this.current.getCpuUsed() == this.current.getAllocated() * this.getCpuPerUnit()) {
            this.setCongested(true);
        } else {
            this.setCongested(false);
        }

        // get parents and children
        this.parents = componentConfig.getParents();
        this.children = componentConfig.getChildren();
    }
}
