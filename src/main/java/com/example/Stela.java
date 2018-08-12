package com.example;

import java.util.*;

/**
 * Created by wiz on 6/4/18.
 */
public class Stela {

    // assumes current allocation is applied to topology projected
    public List<EtpCell> getEtpTable(Topology topology) {
        List<EtpCell> etpTable = new LinkedList<EtpCell>();

        // get congested components
        List<String> congested = topology.getCongested();

        // get projected throughput
        long topologyThrougput = topology.getProjectedThroughput();

        // foreach component calculate etp
        Map<String, Component> components = topology.getComponents();
        for (Map.Entry<String, Component> entry : components.entrySet()) {
            Component component = entry.getValue();

            long childThroughput = this.getChildThroughput(topology, component.getName());
            Double etp = (double)childThroughput/topologyThrougput;

            EtpCell etpCell = new EtpCell(component.getName(), etp.doubleValue());
            etpTable.add(etpCell);
       }

        return etpTable;
    }

    public long getChildThroughput(Topology topology, String component) {
        Queue<String> queue = new LinkedList<String>();
        Queue<String> leaves = new LinkedList<String>();
        queue.add(component);

        while(false == queue.isEmpty()) {
            String compName = queue.remove();
            Component comp = topology.getComponent(compName);

            for (Map.Entry<String, Double> child : comp.getChildren().entrySet()) {
                Component childComponent = topology.getComponent(child.getKey());
                if(childComponent.isCongestedProjected() == false) {
                    queue.add(child.getKey());
                }

                if(childComponent.getChildren().size() == 0)
                    leaves.add(child.getKey());
            }
        }

        long childThroughput = 0;
        for (String leaf : leaves) {
            Component comp = topology.getComponent(leaf);
            childThroughput += comp.getProjected().getOut();
        }

        return childThroughput;
    }

    public void dumpEtpTable(List<EtpCell> etpTable) {
        for (EtpCell cell : etpTable) {
            cell.dump();
        }
    }

    public AllocationMap getOptimalAllocation(Topology topology, long freeResources) {
        AllocationMap allocationMap = new AllocationMap();

        // get allocator table
        List<EtpCell> etpTable = this.getEtpTable(topology);
        this.dumpEtpTable(etpTable);

        System.out.println("~~~~~~~~   Sorted  ~~~~~~~~~~~");

        // sort table to get the roi sorting
        // Collections.sort(allocatorTable);
        // Collections.reverse(allocatorTable);
        // this.dumpAllocatorTable(allocatorTable);

        // get optimal allocation
        // AllocationMap optimalMap = this.getOptimalAllocationMap(allocatorTable, freeResources);

        return allocationMap;
    }
}