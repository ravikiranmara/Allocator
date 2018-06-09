package com.example;

/**
 * Created by wiz on 6/4/18.
 */
public class AllocatorCell {
    private long resourceAllocated;
    private long throughputIncrease;
    private AllocationMap allocationMap;
    private double roi;
    private String headComponent;

    // getter and setters
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

    public void dump() {
        System.out.println();
        System.out.println("Allocator Cell :-");
        System.out.println("Resource Allocated : " + this.getResourceAllocated());
        System.out.println("Throughput Increase : " + this.getThroughputIncrease());
        System.out.println("Roi : " + this.getRoi());
        this.getAllocationMap().dump();
    }
}
