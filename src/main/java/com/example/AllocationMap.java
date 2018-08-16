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

    public void addAllocationForComponent(String component, long value) {
        if(null == this.allocationMap.get(component)) {
            this.setAllocationForComponent(component, value);
            return;
        }
        this.setAllocationForComponent(component, this.allocationMap.get(component) + value);
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

    public void removeAllocation(AllocationMap ralloc) {
         for (Map.Entry<String, Long> entry : ralloc.getAllocationMap().entrySet()) {
             Long remres = entry.getValue();
             Long nres = allocationMap.get(entry.getKey());
             if (nres == null || nres == 0 || remres-nres < 0) {
                 System.out.println("This shouldn't happen. the allocation does not exist to remove");
             }

             this.setAllocationForComponent(entry.getKey(), remres - nres);
        }
    }

    public void addAllocation(AllocationMap alloc) {
        for (Map.Entry<String, Long> entry : alloc.getAllocationMap().entrySet()) {
             Long remres = entry.getValue();
             Long nres = this.allocationMap.get(entry.getKey());
             if (nres == null)
                 nres = 0L;

             // System.out.println("Set comp : " + entry.getKey() + ", " + (remres+nres));
             this.setAllocationForComponent(entry.getKey(), remres + nres);
        }
    }

    public long getResourceAllocationForComponent(String comp) {
        return (null == this.allocationMap.get(comp))? 0 :
                this.allocationMap.get(comp);
    }
}
