package com.example;


import java.io.IOException;
import java.util.*;

/**
 * Created by wiz on 5/28/18.
 */
public class Allocator {

    public static void main(String[] args) {

        try {
            System.out.println("Enter num Resources : ");
            Scanner in = new Scanner(System.in);
            long freeResources = in.nextLong();

            // Read file
            String filePath = ("//home//wiz//workspace//GIT//Allocator//test6A_case1.json");
            // String filePath = new String("//home//wiz//junk//test11.json");
            FileParser fileParser = new FileParser(filePath);

            // fileParser.dumpTopology();
            System.out.println("Reading topology from : " + filePath);
            TopologyConfig topologyConfig = fileParser.readTopologyConfig();
            Topology topology = new Topology(topologyConfig);
            // topology.dump();



            System.out.println("============    EXECUTE FOR ESTELA     ==================");
            // get allocator map for estela
            Estela estela = new Estela();

            // System.out.println("Get Optimal Map for freeResources : " + freeResources);
            System.out.println("congested Count : " + topology.getCongestedProjected().size());

            Topology sendTopology = new Topology(topology);
            // topology.dump();
            AllocationMap optimalMap = Allocator.getEstelaAllocation(sendTopology, freeResources);
            System.out.println("Estela : dump optimal map");
            optimalMap.dump();
            topology.propogateAllocation(optimalMap);
            long optincrease = topology.getProjectedThroughput() - topology.getCurrentThroughput();
            System.out.println("projected increase in throughput (optimal) : " + optincrease);
            System.out.println("congested Count : " + topology.getCongestedProjected().size());
            for (String cong : topology.getCongestedProjected()) System.out.println(cong);

            System.out.println("============    EXECUTE FOR STELA     ==================");

            topology.refreshComponentProjected();
            // get allocator map for Stela
            Stela stela = new Stela();
            topology.refreshComponentProjected();
            AllocationMap optimalStelaMap = stela.getOptimalAllocation(topology, freeResources);
            optimalStelaMap.dump();

            topology.propogateAllocation(optimalStelaMap);
            long stelaIncrease = topology.getProjectedThroughput() - topology.getCurrentThroughput();
            System.out.println("projected increase in throughput : " + stelaIncrease);
            System.out.println("Congested Count : " + topology.getCongestedProjected().size());
            for (String cong : topology.getCongestedProjected()) System.out.println(cong);

        } catch (IOException ex) {
            System.out.println("IO exception while reading filename");
        }
    }

    public static AllocationMap getEstelaAllocation(Topology oldTopology, long freeResources) {
        // get allocator map for estela
        Estela estela = new Estela();
        AllocationMap optimalMap = new AllocationMap();
        Topology topology = new Topology(oldTopology);

        // System.out.println("Get Optimal Map for freeResources : " + freeResources);
        System.out.println("congested Count : " + topology.getCongestedProjected().size());
        optimalMap = estela.getOptimalAllocation(topology, freeResources);
        System.out.println("Estela : dump optimal map");
        optimalMap.dump();

        topology.propogateAllocation(optimalMap);
        long optincrease = topology.getProjectedThroughput() - topology.getCurrentThroughput();
        System.out.println("projected increase in throughput (optimal) : " + optincrease);
        System.out.println("congested Count : " + topology.getCongestedProjected().size());

        List<AllocatorCell> usedAllocation = new LinkedList<AllocatorCell>();
        List<AllocatorCell> allocatorTable = new LinkedList<AllocatorCell>();
        AllocationMap subOptimalMap = optimalMap;

        long remainingResources = freeResources-subOptimalMap.getTotalAllocatedAdditionalResources();
        while (remainingResources > 0) {
            allocatorTable = estela.getAllocatorTable();
            usedAllocation = estela.calculateUsedAllocation(subOptimalMap);
            allocatorTable.removeAll(usedAllocation);

            if(allocatorTable.isEmpty())
                break;

            // separate this to a function {
            AllocatorCell lowNumCell = allocatorTable.get(0);
            for (AllocatorCell cell : allocatorTable) {
                if(cell.getResourceAllocated() < lowNumCell.getResourceAllocated())
                    lowNumCell = cell;
            }

            AllocatorCell lowCell = allocatorTable.get(0);
            for (AllocatorCell rcell : allocatorTable) {
                if (rcell.getResourceAllocated() == lowNumCell.getResourceAllocated())
                    if(rcell.getRoi() < lowCell.getRoi())
                        lowCell = rcell;
            }
            // }

            List<Component> subComponents = new LinkedList<Component>();
            Component head = topology.getComponent(lowCell.getHeadComponent());
            List<String> nullHead = new LinkedList<String>();
            nullHead.add("null");
            head.setParents(nullHead);

            Queue<String> queue = new LinkedList<String>();
            queue.add(lowCell.getHeadComponent());
            while (queue.isEmpty() != true) {
                Component comp = topology.getComponent(queue.remove());
                subComponents.add(comp);

                for (Map.Entry<String, Double> child : comp.getChildren().entrySet()) {
                    queue.add(child.getKey());
                }
            }

            // create sub component topology
            topology = new Topology(subComponents);
            AllocationMap headMap = new AllocationMap();
            headMap.addAllocationForComponent(head.getName(), 1);
            remainingResources -= 1;
            topology.propogateAllocation(headMap);

            estela.getOptimalAllocation(topology,remainingResources);
            subOptimalMap = estela.getOptimalAllocation(topology, remainingResources);

            remainingResources = remainingResources - subOptimalMap.getTotalAllocatedAdditionalResources();
            optimalMap.addAllocation(subOptimalMap);
            optimalMap.addAllocation(headMap);

            topology.propogateAllocation(optimalMap);
        }

        optimalMap.dump();

        // print stats for final map
        topology.propogateAllocation(optimalMap);
        long increase = topology.getProjectedThroughput() - topology.getCurrentThroughput();
        System.out.println("projected increase in throughput (optimal + greedy): " + increase);
        System.out.println("Congested Count : " + topology.getCongestedProjected().size());
        for (String cong : topology.getCongestedProjected()) System.out.println(cong);

        return optimalMap;
    }
}
