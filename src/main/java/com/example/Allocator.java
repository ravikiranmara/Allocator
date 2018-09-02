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
            String filePath = ("//home//wiz//git//Allocator//test9A.json");
            // String filePath = new String("//home//wiz//junk//test11.json");
            FileParser fileParser = new FileParser(filePath);

            // fileParser.dumpTopology();
            System.out.println("Reading topology from : " + filePath);
            TopologyConfig topologyConfig = fileParser.readTopologyConfig();
            Topology topology = new Topology(topologyConfig);
            // topology.dump();

            System.out.println("============    EXECUTE FOR ESTELA     ==================");

            // System.out.println("Get Optimal Map for freeResources : " + freeResources);
            System.out.println("Initial Congested Count : " + topology.getCongestedProjected().size());

            Topology sendTopology = new Topology(topology);
            // topology.dump();
            AllocationMap optimalMap = Allocator.getEstelaAllocation(sendTopology, freeResources);
            /*
            System.out.println("Estela : dump optimal map");
            optimalMap.dump();
            topology.propogateAllocation(optimalMap);
            long optincrease = topology.getProjectedThroughput() - topology.getCurrentThroughput();
            System.out.println("projected increase in throughput (optimal) : " + optincrease);
            System.out.println("Congested Count : " + topology.getCongestedProjected().size());
            for (String cong : topology.getCongestedProjected()) System.out.println(cong);
            */

            System.out.println("============    EXECUTE FOR STELA     ==================");

            sendTopology = new Topology(topology);
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

    public static AllocationMap getEstelaAllocation(Topology topology, long freeResources) {
        // get allocator map for estela
        Estela2 estela = new Estela2();
        AllocationMap optimalMap = new AllocationMap();
        // Topology topology = new Topology(oldTopology);

        // System.out.println("Get Optimal Map for freeResources : " + freeResources);
        System.out.println("congested Count : " + topology.getCongestedProjected().size());
        optimalMap = estela.getOptimalAllocation(topology, freeResources);
        System.out.println("Estela : dump optimal map");
        optimalMap.dump();

        topology.propogateAllocation(optimalMap);
        long optincrease = topology.getProjectedThroughput() - topology.getCurrentThroughput();
        System.out.println("Estela::projected increase in throughput (optimal) : " + optincrease);
        System.out.println("Estela::congested Count : " + topology.getCongestedProjected().size());

        List<AllocatorCell> usedAllocation = new LinkedList<AllocatorCell>();
        List<AllocatorCell> allocatorTable = new LinkedList<AllocatorCell>();
        AllocationMap subOptimalMap = new AllocationMap(optimalMap);
        Topology subOptTopoogy = new Topology(topology);

        long remainingResources = freeResources-subOptimalMap.getTotalAllocatedAdditionalResources();
        System.out.println("Estela::(SubOptimal) remaining resources - " + remainingResources);
        while (remainingResources > 0) {
            // remove used items from allocation table
            allocatorTable = estela.getAllocatorTable();
            usedAllocation = estela.calculateUsedAllocation(subOptimalMap);
            allocatorTable.removeAll(usedAllocation);

            if(allocatorTable.isEmpty())
                break;

            System.out.println("Estela::(SubOptimal)-unused allocations");
            for (AllocatorCell cell : allocatorTable)
                cell.dump();

            // Get cell that needs lowest resources {
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

            // prepare new topology with the subtree of the chosen allocation
            List<Component> subComponents = new LinkedList<Component>();
            Component head = subOptTopoogy.getComponent(lowCell.getHeadComponent());
            List<String> nullHead = new LinkedList<String>();
            nullHead.add("null");
            head.setParents(nullHead);

            Queue<String> queue = new LinkedList<String>();

            // need to add our head, instead of from topology
            subComponents.add(head);
            for (Map.Entry<String, Double> child : head.getChildren().entrySet()) {
                queue.add(child.getKey());
            }

            while (queue.isEmpty() != true) {
                Component comp = subOptTopoogy.getComponent(queue.remove());
                subComponents.add(comp);

                for (Map.Entry<String, Double> child : comp.getChildren().entrySet()) {
                    queue.add(child.getKey());
                }
            }

            // create sub component topology
            subOptTopoogy = new Topology(subComponents);

            AllocationMap headMap = new AllocationMap();
            headMap.addAllocationForComponent(head.getName(), 1);
            remainingResources -= 1;
            subOptTopoogy.propogateAllocation(headMap);

            // get allocation for
            System.out.println("----------------GetSubOptimalResource--------------");
            subOptimalMap = estela.getOptimalAllocation(topology, remainingResources);

            remainingResources = remainingResources - subOptimalMap.getTotalAllocatedAdditionalResources();
            System.out.println("remres count : " + remainingResources);
            optimalMap.addAllocation(subOptimalMap);
            optimalMap.addAllocation(headMap);
            subOptimalMap.dump();

            subOptTopoogy.propogateAllocation(optimalMap);
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
