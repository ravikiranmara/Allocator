package com.example;

import java.util.HashMap;
import java.util.Map;
import java.util.*;

/**
 * Created by wiz on 6/4/18.
 * . Get list of congested components
 * . foreach congested component {
 * .     increasingly allocate a resource
 * .     get allocation for subtree
 * .     calculate throughput increase
 * . }
 */
public class Estela {
    List<AllocatorCell> allocatorTable;
    List<AllocatorCell> usedAllocation;

    Estela() {
        allocatorTable = new LinkedList<AllocatorCell>();
        usedAllocation = new LinkedList<AllocatorCell>();
    }

    public List<AllocatorCell> getAllocateTable(Topology topology, long maxResources) {
        List<AllocatorCell> allocatorTable = new ArrayList<AllocatorCell>();
        List<String> congested = topology.getCongested();
        System.out.println("congested components");
        for (String cong : congested) System.out.println(cong);

        for(String comp : congested) {
            Component component = topology.getComponent(comp);
            System.out.println("Processing comp : " + comp);
            for (long res=1; res<=maxResources; res++) {
                long required = topology.getResourcesRequiredAdditionalProjected(component.getName());
                System.out.println("Total additional required for head (allowed : " + res + "): " + required);
                AllocatorCell allocatorCell = new AllocatorCell();

                if (res > required) {
                    // System.out.println("skipping : " + comp + ":" + res);
                    break;
                }

                AllocationMap allocationMap =
                        this.getAllocationForSubtree(component, res, topology);
                // allocationMap.dump();

                // set map
                allocatorCell.setAllocationMap(allocationMap);

                // set throughput
                long throughputIncrease = this.getThroughputIncrease(topology, allocationMap);
                allocatorCell.setThroughputIncrease(throughputIncrease);

                // resource allocated
                long allocatedResources = allocationMap.getTotalAllocatedAdditionalResources();
                allocatorCell.setResourceAllocated(allocatedResources);

                // head resources - does this matter?

                // set roi
                if(allocatedResources != 0)
                    allocatorCell.setRoi((double)throughputIncrease/allocatedResources);
                else allocatorCell.setRoi(0);

                // head component
                allocatorCell.setHeadComponent(comp);

                allocatorTable.add(allocatorCell);
            }
        }

        return allocatorTable;
    }

    public long getThroughputIncrease(Topology topology, AllocationMap allocationMap) {
        topology.propogateAllocation(allocationMap);
        return Math.max(0, topology.getProjectedThroughput() - topology.getCurrentThroughput());
    }

    public AllocationMap getAllocationForSubtree(Component component, long rootRes, Topology topology) {
        AllocationMap allocationMap = new AllocationMap();
        Queue<Component> queue = new LinkedList<Component>();
        long currentAllocated = component.getCurrent().getAllocated();

        // process the root component
        // System.out.println("Calculate allocation for comp : " + component.getName() + ", res : " + rootRes);
        Map<String, Double> rootChildren = component.getChildren();
        for (Map.Entry<String, Double> child : rootChildren.entrySet()) {
            queue.add(topology.getComponent(child.getKey()));
        }
        topology.allocateToProjected(component.getName(), currentAllocated + rootRes);
        allocationMap.setAllocationForComponent(component.getName(), rootRes);

        // walk the queue
        while (queue.isEmpty() != true) {
            Component comp = queue.remove();
            long compCurrentAllocated = comp.getCurrent().getAllocated();
            // System.out.println("Processing comp : " + comp.getName());

            // get all children, add to queue
            Map<String, Double> children = comp.getChildren();
            for (Map.Entry<String, Double> child : children.entrySet()) {
                queue.add(topology.getComponent(child.getKey()));
            }

            // get additional resources required
            long required = topology.getResourcesRequiredProjected(comp.getName());
            // System.out.println("required for comp : " + required);
            topology.allocateToProjected(comp.getName(), required);

            // add resources to allocation map
            long requiredAdditional = topology.getResourcesRequiredAdditionalProjected(comp.getName());
            allocationMap.setAllocationForComponent(comp.getName(), requiredAdditional);
        }

        return allocationMap;
    }

    public List<String> getOverlapping(Map<String, AllocationMap> currentAllocation, AllocationMap testMap, String headComp) {
        List<String> overlap = new ArrayList<String>();

        // head is a special case
        if(currentAllocation.get(headComp) != null) {
            // System.out.println(currentAllocation.get(headComp).getAllocationMap().get(headComp).longValue() + "?" +
            //         testMap.getAllocationMap().get(headComp).longValue());
            if (currentAllocation.get(headComp).getAllocationMap().get(headComp).longValue() <
                    testMap.getAllocationMap().get(headComp).longValue()) {
                // System.out.println("head case");
                overlap.add(headComp);
            }
            return overlap;
        }

        // check if current is child of any previous allocation
        for (Map.Entry<String, AllocationMap> calloc : currentAllocation.entrySet()) {
            AllocationMap allocMap = calloc.getValue();
            for (Map.Entry<String, Long> allocEle : allocMap.getAllocationMap().entrySet()) {
                if (allocEle.getKey().equalsIgnoreCase(headComp)) {
                    //  There is a different superseeding allocation. Can't allocate this
                    System.out.println("Superseeded by current");
                    return null;
                }
            }
        }

        for (Map.Entry<String, Long> componentMap : testMap.getAllocationMap().entrySet()) {
            String component = componentMap.getKey();
            if(currentAllocation.get(component) != null)
                overlap.add(component);
        }

        return overlap;
    }

    public AllocationMap getOptimalAllocationMap(List<AllocatorCell> allocatorTable, long freeResources) {
        AllocationMap optimalMap = new AllocationMap();
        Map<String, AllocationMap> currentAllocation = new HashMap<String, AllocationMap>();
        int tableptr = -1;
        int maxtablePtr = allocatorTable.size();

        while (freeResources > 0) {
            tableptr++;
            System.out.println("-------------------- Get Optimal Allocation ---------------------");
            System.out.println("System resources : " + freeResources + "table Ptr : " + tableptr);

            // check if we are at the end of the table
            if(tableptr >= maxtablePtr) {
                System.out.println("Exit end of table : " + tableptr + ":" + maxtablePtr);
                break;
            }

            // get overlapping
            AllocatorCell addCell = allocatorTable.get(tableptr);
            AllocationMap addMap = addCell.getAllocationMap();
            String addComp = addCell.getHeadComponent();
            // System.out.println("** Consider allocation **");
            // addMap.dump();

            if(currentAllocation.get(addComp) != null) {
                if (currentAllocation.get(addComp).getAllocationMap().get(addComp).longValue() >=
                        addMap.getAllocationMap().get(addComp).longValue()) {
                    System.out.println("head case : continue to next entry");
                    continue;
                }
            }


            List<String> overlap = this.getOverlapping(currentAllocation, addMap, addComp);
            if (null == overlap) {
                // case superseeded overlap. no alloc. goto next
                continue;
            }

            // dump for debug
            /*
            System.out.println("List of overlapping allocations : ");
            for (String over :  overlap) {
                System.out.println("\t" + over);
            }
            */

            // get free resources
            List<AllocationMap> allocList = new ArrayList<AllocationMap>();
            for (String alloc : overlap) {
                allocList.add(currentAllocation.get(alloc));
            }

            // check if we have enough resources
            int freeable = 0;
            if(allocList.size() > 0) {
                for (AllocationMap alloc : allocList) {
                    // alloc.dump();
                    freeable += alloc.getTotalAllocatedAdditionalResources();
                }
            }
            long totalFreeResources = freeable + freeResources;
            // System.out.println("Total free resources " + freeable + "+"
            //         + freeResources + " :" + totalFreeResources);

            // continue if not enough resources
            if (totalFreeResources < addMap.getTotalAllocatedAdditionalResources()) {
                System.out.println("not enough resources for this allocation");
                continue;
            }

            // remove all overlapping
            System.out.println("Removing allocations : " + overlap.size());
            for (String alloc : overlap) {
                AllocationMap temp = currentAllocation.get(alloc);
                currentAllocation.remove(alloc);
                optimalMap.removeAllocation(temp);
            }

            // add current allocation
            optimalMap.addAllocation(addMap);
            currentAllocation.put(addComp, addMap);
            usedAllocation.add(addCell);

            // System.out.println("current optimal allocation : ");
            // optimalMap.dump();

            // adjust the available resources after allocation
            freeResources = totalFreeResources - addMap.getTotalAllocatedAdditionalResources();
        }

        return optimalMap;
    }

    public void dumpAllocatorTable(List<AllocatorCell> allocatorTable) {
        for (AllocatorCell allocatorCell : allocatorTable) {
            allocatorCell.dump();
        }
    }

    public AllocationMap getOptimalAllocation(Topology topology, long freeResources) {

        // get allocator table
        allocatorTable = this.getAllocateTable(topology, freeResources);
        // this.dumpAllocatorTable(allocatorTable);

        System.out.println("~~~~~~~~   Sorted  ~~~~~~~~~~~");

        // sort table to get the roi sorting
        Collections.sort(allocatorTable);
        Collections.reverse(allocatorTable);
        // this.dumpAllocatorTable(allocatorTable);

        // get optimal allocation
        AllocationMap optimalMap = this.getOptimalAllocationMap(allocatorTable, freeResources);

        return optimalMap;
    }

    public AllocationMap getGreedyAllocation(Topology topology, long freeResources) {
        allocatorTable.removeAll(usedAllocation);

        if(allocatorTable.isEmpty())
            return new AllocationMap();

        // get map with lowest req resources and highest roi
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

        // lowCell.dump();
        // allocate resources
        String headcomp = lowCell.getHeadComponent();
        AllocationMap oldmap = lowCell.getAllocationMap();
        AllocationMap newMap = new AllocationMap();


        Queue<Component> queue = new LinkedList<Component>();
        queue.add(topology.getComponent(headcomp));

        // bfs traversal. alloc as much as possible
        // alternate is to dfs and allocate for a branch
        while (queue.isEmpty() != true) {
            Component comp = queue.remove();

            long oldmapres = oldmap.getResourceAllocationForComponent(comp.getName());
            long allocNum = Math.min(oldmapres, freeResources);
            newMap.setAllocationForComponent(comp.getName(), allocNum);

            freeResources -= allocNum;
            if (freeResources == 0)
                break;

            // get all children, add to queue
            Map<String, Double> children = comp.getChildren();
            for (Map.Entry<String, Double> child : children.entrySet()) {
                queue.add(topology.getComponent(child.getKey()));
            }
        }

        return newMap;
    }
}