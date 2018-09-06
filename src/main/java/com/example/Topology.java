package com.example;

import java.util.*;

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
    List<String> congestedProjected;
    long currentThroughput;

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

    public List<String> getCongestedProjected() {
        this.calculateCongestedProjected();
        return congestedProjected;
    }

    public Component getComponent(String name) {
        return this.components.get(name);
    }

    public long getInputFromParentProjected(String name) {
        Component component = this.getComponent(name);
        // System.out.println("Input from parent component - " + component.getName());

        // get all parents
        List<String> parents = component.getParents();
        long totalInput = 0;
        if (parents.size() == 1 && parents.get(0).equalsIgnoreCase("null")) {
            totalInput = component.getCurrent().getIn() *
                    component.getProjected().getAllocated();
        } else {
            List<Component> parentcomponents = new ArrayList<Component>();
            for (String parent : parents) {
                // System.out.println("Add parent : " + parents);
                parentcomponents.add(this.getComponent(parent));
            }
            // System.out.println("parents size - " + parentcomponents.size());

            // get input from parents
            for (Component parent : parentcomponents) {
                // System.out.println("parents - " + parent.getName());

                Double ratio = parent.getChildOutputRatio(name);
                long totalOutput = parent.getProjected().getOut();
                // System.out.println("Input ratio from parents of comp - " + totalOutput + ":" + ratio);
                totalInput += ratio * totalOutput;
            }
        }

        // System.out.println("Input from parents of comp - " + name + ":" + totalInput);

        return totalInput;
    }

    public long getResourcesRequiredProjected(String name) {
        long input = this.getInputFromParentProjected(name);
        Component component = this.getComponent(name);

        long required = component.getResourcesForInput(input);
        if (component.getMaxInput(required) == input)
            required+=1;
        // System.out.println("Required for projected comp - " + name + " ( " + input + ") :" + required);
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
        long parentInput = current.getIn() * numResources;
        if (comp.getParents().size() != 0) // spout
            parentInput = getInputFromParentProjected(name);

        long maxInput = comp.getMaxInput(numResources);
        long input = Math.min(maxInput, parentInput);
        long cpu = (long)Math.ceil(input/comp.getInputPerCpu());

        // calculate new input/output
        newProjected.setAllocated(numResources);
        newProjected.setIn(input);
        newProjected.setOut((long)Math.ceil((input * comp.getInputToOutputRatio())));
        newProjected.setAllocated(numResources);
        newProjected.setCpuUsed(cpu);

        // System.out.println(name + " Input : " + input + ", out : " + newProjected.getOut() + ", resource : " + numResources);

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
            // System.out.println("comp : " + entry.getKey() + comp.getChildren().size());
            if (comp.getParents().size() == 0 ||
                    (comp.getParents().size() == 1
                     && comp.getParents().get(0).equalsIgnoreCase("null"))
            ) {
                this.spout.add(entry.getKey());
            }
        }
    }

    public void calculateLeaves() {
        this.leaves = new ArrayList<String>();
        for (Map.Entry<String, Component> entry : components.entrySet()) {
            Component comp = entry.getValue();
            if (comp.getChildren().size() == 0) {
                // System.out.println("Adding leaf : " + comp.getName() + comp.getChildren().size());
                this.leaves.add(entry.getKey());
            }
        }
        // System.out.println("leaves:-" + this.getLeaves().size());
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

    public List<String> calculateCongested(List<String> anchors) {
        List<String> congested = new ArrayList<String>();

        Queue<Component> queue = new LinkedList<Component>();
        for (String comps : anchors) {
            queue.add(this.components.get(comps));
        }

        while (queue.isEmpty() != true) {
            Component comp = queue.remove();

            if (comp.isCongested()) {
                this.congested.add(comp.getName());
            }
        }

        return congested;
    }

    public List<String> calculateCongestedProjected(List<String> anchors) {
        List<String> congested = new ArrayList<String>();

        Queue<Component> queue = new LinkedList<Component>();
        for (String comps : anchors) {
            queue.add(this.components.get(comps));
        }

        while (queue.isEmpty() != true) {
            Component comp = queue.remove();

            if (comp.isCongestedProjected()) {
                this.congested.add(comp.getName());
            }
        }

        return congested;
    }


    public void calculateCongestedProjected() {
        this.congestedProjected = new ArrayList<String>();
        for (Map.Entry<String, Component> entry : components.entrySet()) {
            Component comp = entry.getValue();
            if (comp.isCongestedProjected()) {
                this.congestedProjected.add(entry.getKey());
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

        // System.out.println("getCurrentThroughput::projected : " + totalOutput);
        return totalOutput;
    }

    public long getProjectedThroughput() {
        long totalOutput = 0;
        for (String leaf : this.getLeaves()) {
            Component comp = this.getComponent(leaf);
            totalOutput += comp.getProjected().getOut();
        }

        // System.out.println("getProjectedThroughput::projected : " + totalOutput);
        return totalOutput;
    }

    public void propogateAllocation(AllocationMap allocationMap) {
        List<String> spouts = this.getSpout();
        Queue<Component> queue = new LinkedList<Component>();

        // this logic is flawed.. all spouts must be updated
        for (String spout : spouts) {
            queue.add(this.getComponent(spout));
        }

        // System.out.println("propogate");
        while (queue.isEmpty() != true) {
            Component comp = queue.remove();

            // get all child
            for (Map.Entry<String, Double> child : comp.getChildren().entrySet()) {
                queue.add(this.getComponent(child.getKey()));
            }

            // if comp is spout, copy current to projected
            long allocated = this.getComponent(comp.getName()).getCurrent().getAllocated();
            allocated += allocationMap.getResourceAllocationForComponent(comp.getName());
            ComponentState projState = this.getProjectedForState(comp.getName(), allocated);
            comp.setProjected(projState);

            // System.out.println("Projected for comp : " + comp.getName() + " " + comp.getProjected().getOut());
            // projState.dump();
        }

        return;
    }

    public void initialize() {
        components = new HashMap<String, Component>();
        spout = new ArrayList<String>();
        leaves = new ArrayList<String>();
        congested = new ArrayList<String>();
        congestedProjected = new ArrayList<String>();
    }

    void refreshComponentProjected() {
        for (Map.Entry<String, Component> entry : this.getComponents().entrySet()) {
            Component comp = entry.getValue();
            comp.setProjected(comp.getCurrent());
        }
    }

    Topology (List<Component> topology) {
        this.initialize();

        for (Component comp : topology) {
            Component c = new Component(comp);
            this.components.put(c.getName(), c);
        }

        // now initialize supporting data
        this.calculateSpouts();
        this.calculateLeaves();
        this.calculateCongested();
    }

    Topology (Topology topology) {
        this.initialize();

        for (Map.Entry<String, Component> entry : topology.getComponents().entrySet()) {
            Component comp = new Component(entry.getValue());
            this.components.put(comp.getName(), comp);

        }

        // now initialize supporting data
        this.calculateSpouts();
        this.calculateLeaves();
        this.calculateCongested();
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
