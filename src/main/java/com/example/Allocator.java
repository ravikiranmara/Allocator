package com.example;


import java.io.IOException;
import java.util.*;

/**
 * Created by wiz on 5/28/18.
 */
public class Allocator {

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

            // get allocator map for estela
            Estela estela = new Estela();
            AllocationMap optimalMap = estela.getOptimalAllocation(allocatorTable, freeResources);
            System.out.println("Estela : dump optimal map");
            optimalMap.dump();

            // get allocator map for Stela


        } catch (IOException ex) {
            System.out.println("IO exception while reading filename");
        }
    }
}
