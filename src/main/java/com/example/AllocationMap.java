package com.example;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wiz on 6/4/18.
 */
public class AllocationMap {
    private Map<String, Integer> allocationMap;

    AllocationMap() {
        allocationMap = new HashMap<String, Integer>();
    }

    public void incrementAllocation(String component) {
        Integer val = this.allocationMap.get(component);
        if (null == val)
            val = 0;

        this.allocationMap.put(component, val+1);
    }

    public void setAllocationForComponent(String component, int value) {
        this.allocationMap.put(component, value);
    }

    public Map<String, Integer> getAllocationMap() {
        return this.allocationMap;
    }
}
