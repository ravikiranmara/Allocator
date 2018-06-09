package com.example;


import java.io.IOException;
import java.util.*;

/**
 * Created by wiz on 5/28/18.
 */
public class Allocator {

    // constructor
    Allocator() {
    }

    public List<AllocatorCell> getAllocateTable(Topology topology, int maxResources) {
        List<AllocatorCell> allocatorTable = new ArrayList<AllocatorCell>();
        List<String> congested = topology.getCongested();

        for(String comp : congested) {
            Component component = topology.getComponent(comp);
            for (int res=0; res<maxResources; res++) {
                long required = topology.getResourcesRequiredAdditionalProjected(component.getName());
                AllocatorCell allocatorCell = new AllocatorCell();

                if (res > required) {
                    System.out.println("skipping : " + comp + ":" + res);
                    break;
                }

                AllocationMap allocationMap =
                    this.getAllocationForSubtree(component, res, topology);

                // set map
                allocatorCell.setAllocationMap(allocationMap);

                // set throughput
                long throughputIncrease = this.getThroughputIncrease(topology, allocationMap);
                allocatorCell.setThroughputIncrease(throughputIncrease);

                // resource allocated
                long allocatedResources = allocationMap.getTotalAllocatedAdditionalResources();
                allocatorCell.setResourceAllocated(allocatedResources);

                // head resources - does this matter?

                // set roi
                if(allocatedResources != 0)
                    allocatorCell.setRoi((double)throughputIncrease/allocatedResources);
                else allocatorCell.setRoi(0);

                allocatorTable.add(allocatorCell);
            }
        }

        return allocatorTable;
    }

    public long getThroughputIncrease(Topology topology, AllocationMap allocationMap) {
        topology.makeMapAllocation(allocationMap);
        return Math.max(0, topology.getProjectedThroughput() - topology.getCurrentThroughput());
    }

    public AllocationMap getAllocationForSubtree(Component component, int rootRes, Topology topology) {
        AllocationMap allocationMap = new AllocationMap();
        Queue<Component> queue = new LinkedList<Component>();
        long currentAllocated = component.getCurrent().getAllocated();

        // process the root component
        System.out.println("Calculate allocation for comp : " + component.getName() + ", res : " + rootRes);
        Map<String, Double> rootChildren = component.getChildren();
        for (Map.Entry<String, Double> child : rootChildren.entrySet()) {
            queue.add(topology.getComponent(child.getKey()));
        }
        topology.allocateToProjected(component.getName(), currentAllocated + rootRes);
        allocationMap.setAllocationForComponent(component.getName(), rootRes);

        // walk the queue
        while (queue.isEmpty() != true) {
            Component comp = queue.remove();
            long compCurrentAllocated = comp.getCurrent().getAllocated();
            System.out.println("Processing comp : " + comp.getName());

            // get all children, add to queue
            Map<String, Double> children = comp.getChildren();
            for (Map.Entry<String, Double> child : children.entrySet()) {
                queue.add(topology.getComponent(child.getKey()));
            }

            // get additional resources required
            long required = topology.getResourcesRequiredProjected(comp.getName());
            topology.allocateToProjected(comp.getName(), required);

            // add resources to allocation map
            long requiredAdditional = topology.getResourcesRequiredAdditionalProjected(comp.getName());
            allocationMap.setAllocationForComponent(comp.getName(), requiredAdditional);
        }

        return allocationMap;
    }

    public void sortAllocatorTable(List<AllocatorCell> allocatorTable) {
        // sort by roi
        
        // sort by resources
    }

    public void dumpAllocatorTable(List<AllocatorCell> allocatorTable) {
        for (AllocatorCell allocatorCell : allocatorTable) {
            allocatorCell.dump();
        }
    }

    public static void main(String[] args) {
        try {
            System.out.println("Enter file path : ");
            int freeResources = 15;

            // Read file
            String filePath = new String("//home//wiz//junk//test.json");
            FileParser fileParser = new FileParser(filePath);

            // fileParser.dumpTopology();
            TopologyConfig topologyConfig = fileParser.readTopologyConfig();
            Topology topology = new Topology(topologyConfig);
            topology.dump();

            // call allocator to get allocate estimate
            Allocator allocator = new Allocator();

            List<AllocatorCell> allocatorTable = allocator.getAllocateTable(topology, freeResources);
            allocator.dumpAllocatorTable(allocatorTable);

            System.out.println("~~~~~~~~   Sorted  ~~~~~~~~~~~");

            Collections.sort(allocatorTable);
            Collections.reverse(allocatorTable);
            allocator.dumpAllocatorTable(allocatorTable);

        } catch (IOException ex) {
            System.out.println("IO exception while reading filename");
        }
    }
}
