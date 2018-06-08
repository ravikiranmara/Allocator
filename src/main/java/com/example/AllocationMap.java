package com.example;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wiz on 6/4/18.
 */
public class AllocationMap {
    private Map<String, Long> allocationMap;

    AllocationMap() {
        allocationMap = new HashMap<String, Long>();
    }

    public void incrementAllocation(String component) {
        Long val = this.allocationMap.get(component);
        if (null == val)
            val = new Long(0);

        this.allocationMap.put(component, val+1);
    }

    public void setAllocationForComponent(String component, long value) {
        this.allocationMap.put(component, value);
    }

    public Map<String, Long> getAllocationMap() {
        return this.allocationMap;
    }

    public void dump() {
        System.out.println("Dumping Allocation Map :- ");
        for (Map.Entry<String, Long> entry : allocationMap.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
    }

    public Long getTotalAllocatedAdditionalResources() {
        long total = 0;

        for (Map.Entry<String, Long> entry : allocationMap.entrySet()) {
            total += entry.getValue().longValue();
        }

        return total;
    }
}
