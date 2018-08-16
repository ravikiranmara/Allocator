package com.example;

import java.util.*;

/**
 * Created by wiz on 6/4/18.
 */
public class Stela {

    // assumes current allocation is applied to topology projected
    public List<EtpCell> getEtpTable(Topology topology) {
        List<EtpCell> etpTable = new LinkedList<EtpCell>();

        // get projected throughput
        long topologyThrougput = topology.getProjectedThroughput();

        // foreach component calculate etp
        // Map<String, Component> components = topology.getComponents();
        List<String> congested = topology.getCongestedProjected();
        for (String  entry : congested) {
            Component component = topology.getComponent(entry);
            if (component.getParents().size() == 0) {
                // System.out.println("Ignore parent");
                continue;
            }

            long childThroughput = this.getChildThroughput(topology, component.getName());
            // System.out.println("Child Throughput " + entry + " : " + childThroughput);
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
            }

            if(comp.getChildren().size() == 0)
                leaves.add(comp.getName());
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
        System.out.println("Stela : Get Optimal Allocation");
        AllocationMap allocationMap = new AllocationMap();

        for (int i=0; i<freeResources; i++) {
            // get allocator table
            List<EtpCell> etpTable = this.getEtpTable(topology);
            // this.dumpEtpTable(etpTable);

            // System.out.println("~~~~~~~~   Highest ETP ~~~~~~~~~~~");

            // sort table to get the roi sorting
            if (etpTable.size() ==  0) {
                etpTable.add(new EtpCell(topology.getSpout().get(0), 1.0));
            }
            EtpCell highCell = etpTable.get(0);
            for (EtpCell cell : etpTable) {
                if (highCell.getEtp() < cell.getEtp()) {
                    highCell = cell;
                }
            }

            // highCell.dump();

            allocationMap.addAllocationForComponent(highCell.getComponent(), 1);

            topology.propogateAllocation(allocationMap);
        }

        return allocationMap;
    }
}