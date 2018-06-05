package com.example;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by wiz on 5/28/18.
 */
public class Allocator {

    // constructor
    Allocator() {
    }

    public Map<String, AllocatorCell> getAllocateTable(Topology topology, int maxResources) {
        Map<String, AllocatorCell> allocatorTable = new HashMap<String, AllocatorCell>();

        List<String> congested = topology.getCongested();

        for(String comp : congested) {
            Component component = topology.getComponent(comp);
            for (int i=0; i<maxResources; i++) {
                getAllocationForSubtree(component, i, topology);

                System.out.println("add the allocation to table");
            }
        }

        return allocatorTable;
    }

    public AllocatorCell getAllocationForSubtree(Component component, int rootRes, Topology topology) {
        AllocatorCell cell = new AllocatorCell();
        Queue<Component> queue = new LinkedList<Component>();

        System.out.println("Calculate allocation for comp : " + component.getName() + ", res : " + rootRes);
        queue.add(component);

        // walk the queue
        while (queue.isEmpty() != true) {
            Component comp = queue.remove();

            // get all children, add to queue
            Map<String, Double> children = comp.getChildren();
            for (Map.Entry<String, Double> child : children.entrySet()) {
                queue.add(topology.getComponent(child.getKey()));
            }

            // get total input from parents
            long totalInput = 0;
            if (comp.isCongestedProjected()) {
                System.out.println("component is congested");
                totalInput = topology.getInputFromParentProjected(comp.getName());

                if (comp.name.equalsIgnoreCase(component.getName())) {

                }
            }

            System.out.println("Processing comp : " + comp.getName());
        }

        return cell;
    }

    public static void main(String[] args) {
        try {
            System.out.println("Enter file path : ");
            int freeResources = 3;

            // Read file
            String filePath = new String("//home//wiz//junk//test.json");
            FileParser fileParser = new FileParser(filePath);

            // fileParser.dumpTopology();
            TopologyConfig topologyConfig = fileParser.readTopologyConfig();
            Topology topology = new Topology(topologyConfig);
            topology.dump();

            // call allocator to get allocate estimate
            Allocator allocator = new Allocator();

            Map<String, AllocatorCell> allocatorTable = allocator.getAllocateTable(topology, freeResources);

        } catch (IOException ex) {
            System.out.println("IO exception while reading filename");
        }
    }
}
