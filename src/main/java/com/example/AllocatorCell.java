package com.example;

/**
 * Created by wiz on 6/4/18.
 */
public class AllocatorCell implements Comparable<AllocatorCell> {
    private long resourceAllocated;
    private long throughputIncrease;
    private AllocationMap allocationMap;
    private double roi;
    private String headComponent;
    private long congestedBranches;

    // getter and setters
    public long getCongestedBranches() {
        return this.congestedBranches;
    }

    public void setCongestedBranches(long congestedBranches) {
        this.congestedBranches = congestedBranches;
    }

    public long getResourceAllocated() {
        return resourceAllocated;
    }

    public void setResourceAllocated(long resourceAllocated) {
        this.resourceAllocated = resourceAllocated;
    }

    public long getThroughputIncrease() {
        return throughputIncrease;
    }

    public void setThroughputIncrease(long throughputIncrease) {
        this.throughputIncrease = throughputIncrease;
    }

    public AllocationMap getAllocationMap() {
        return allocationMap;
    }

    public void setAllocationMap(AllocationMap allocationMap) {
        this.allocationMap = allocationMap;
    }

    public double getRoi() {
        return roi;
    }

    public void setRoi(double roi) {
        this.roi = roi;
    }

    public String getHeadComponent() {
        return headComponent;
    }

    public void setHeadComponent(String headComponent) {
        this.headComponent = headComponent;
    }

    public int compareTo(AllocatorCell allocatorCell) {
        // we might not need to consider this as separate case
        if (this.getCongestedBranches() > allocatorCell.getCongestedBranches()) {
            return -1;
        } else if (this.getCongestedBranches() < allocatorCell.getCongestedBranches()) {
            return 1;
        }
        // else if congested branches is same

        if(this.getRoi() > allocatorCell.getRoi()) {
            return 1;
        } else if (this.getRoi() < allocatorCell.getRoi()) {
            return -1;
        } else {
            if (this.getResourceAllocated() < allocatorCell.getResourceAllocated())
                return 1;
            else return -1;
        }
    }

    public void dump() {
        System.out.println();
        System.out.println("Allocator Cell :-");
        System.out.println("Head: " + this.getHeadComponent());
        System.out.println("Resource Allocated : " + this.getResourceAllocated());
        System.out.println("Throughput Increase : " + this.getThroughputIncrease());
        System.out.println("Roi : " + this.getRoi());
        this.getAllocationMap().dump();
    }
}
