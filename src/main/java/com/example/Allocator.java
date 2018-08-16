package com.example;


import java.io.IOException;
import java.util.Scanner;

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
            AllocationMap optimalMap = estela.getOptimalAllocation(topology, freeResources);
            System.out.println("Estela : dump optimal map");
            optimalMap.dump();
            topology.propogateAllocation(optimalMap);
            long optincrease = topology.getProjectedThroughput() - topology.getCurrentThroughput();
            System.out.println("projected increase in throughput (optimal) : " + optincrease);
            System.out.println("congested Count : " + topology.getCongestedProjected().size());

            long remainingResources = freeResources-optimalMap.getTotalAllocatedAdditionalResources();
            if (remainingResources > 0) {
                AllocationMap greedyMap = estela.getGreedyAllocation(topology, remainingResources);
                // greedyMap.dump();

                optimalMap.addAllocation(greedyMap);
                optimalMap.dump();
            }

            // print stats for final map
            topology.propogateAllocation(optimalMap);
            long increase = topology.getProjectedThroughput() - topology.getCurrentThroughput();
            System.out.println("projected increase in throughput (optimal + greedy): " + increase);
            System.out.println("Congested Count : " + topology.getCongestedProjected().size());
            for (String cong : topology.getCongestedProjected()) System.out.println(cong);


            System.out.println("============    EXECUTE FOR STELA     ==================");

            topology.refreshComponentProjected();
            // get allocator map for Stela
            Stela stela = new Stela();
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
}
