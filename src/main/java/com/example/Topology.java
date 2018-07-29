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
        System.out.println("\n=====================================================================");
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
        System.out.println("\n=====================================================================");
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

    public Component getComponent(String name) {
        return this.components.get(name);
    }

    public long getInputFromParentProjected(String name) {
        Component component = this.getComponent(name);

        // get all parents
        List<String> parents = component.getParents();
        List<Component> parentcomponents = new ArrayList<Component>();
        for (String parent : parents) {
            parentcomponents.add(this.getComponent(parent));
        }

        // get input from parents
        long totalInput = 0;
        for (Component parent : parentcomponents) {
            Double ratio = parent.getChildOutputRatio(name);
            long totalOutput = parent.getProjected().getOut();
            totalInput += ratio * totalOutput;
        }

        System.out.println("Input from parents of comp - " + name + ":" + totalInput);

        return totalInput;
    }

    public long getResourcesRequiredProjected(String name) {
        long input = this.getInputFromParentProjected(name);
        Component component = this.getComponent(name);

        long required = component.getResourcesForInput(input);
        System.out.println("Required for projected comp - " + name + " ( " + input + ") :" + required);
        return required;
    }

    public long getResourcesRequiredAdditionalProjected(String name) {
        long needed = this.getResourcesRequiredProjected(name);
        long allocated = this.getComponent(name).getCurrent().getAllocated();
        return (needed < allocated)? 0 : (needed - allocated);
    }

    public ComponentState getProjectedForState(String name, long numResources) {
        Component comp = this.getComponent(name);
        ComponentState newProjected = new ComponentState();
        ComponentState current = comp.getCurrent();
        // System.out.println("Allocate to projected : " + name  + ":" + numResources);

        // get input
        long parentInput = getInputFromParentProjected(name);
        long maxInput = comp.getMaxInput(numResources);
        long input = Math.min(maxInput, parentInput);
        long cpu = (long)Math.ceil(input/comp.getInputPerCpu());

        // calculate new input/output
        newProjected.setAllocated(numResources);
        newProjected.setIn(input);
        newProjected.setOut((long)Math.ceil((input * comp.getInputToOutputRatio())));
        newProjected.setAllocated(numResources);
        newProjected.setCpuUsed(cpu);

        System.out.println("Input : " + input + ", out : " + newProjected.getOut() + ", resource : " + numResources);

        return newProjected;
    }

    public void allocateToProjected(String name, long numResources) {
        Component comp = this.getComponent(name);
        ComponentState newState = this.getProjectedForState(name, numResources);

        // set it back
        comp.setProjected(newState);
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
            if (comp.isCongested()) {
                this.congested.add(entry.getKey());
            }
        }
    }

    public void cleanProjected() {
        for (Map.Entry<String, Component> entry : components.entrySet()) {
            Component comp = entry.getValue();
            comp.setProjected(comp.getCurrent());
        }
    }

    public void makeMapAllocation(AllocationMap allocationMap) {
        this.cleanProjected();
        for (Map.Entry<String, Long> alloc : allocationMap.getAllocationMap().entrySet()) {
            Component comp = this.getComponent(alloc.getKey());
            long totalResources = comp.getCurrent().getAllocated() + alloc.getValue();
            this.allocateToProjected(alloc.getKey(), totalResources);
        }
    }

    public long getCurrentThroughput() {
        long totalOutput = 0;
        for (String leaf : this.getLeaves()) {
            Component comp = this.getComponent(leaf);
            totalOutput += comp.getCurrent().getOut();
        }

        return totalOutput;
    }

    public long getProjectedThroughput() {
        long totalOutput = 0;
        for (String leaf : this.getLeaves()) {
            Component comp = this.getComponent(leaf);
            totalOutput += comp.getProjected().getOut();
        }

        return totalOutput;
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
