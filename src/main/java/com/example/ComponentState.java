package com.example;

/**
 * Created by wiz on 6/1/18.
 */
public class ComponentState {
    private long in;
    private long out;
    private long allocated;
    private long cpuUsed;

    // getter and setter
    public long getIn() {
        return in;
    }

    public void setIn(long in) {
        this.in = in;
    }

    public long getOut() {
        return out;
    }

    public void setOut(long out) {
        this.out = out;
    }

    public long getAllocated() {
        return allocated;
    }

    public void setAllocated(long allocated) {
        this.allocated = allocated;
    }

    public long getCpuUsed() {
        return cpuUsed;
    }

    public void setCpuUsed(long cpuUsed) {
        this.cpuUsed = cpuUsed;
    }

    public void copy(ComponentState newState) {
        this.setIn(newState.getIn());
        this.setOut(newState.getOut());
        this.setAllocated(newState.getAllocated());
        this.setCpuUsed(newState.getAllocated());
    }

    public void dump() {
        System.out.println("ComponentState");
        System.out.println("\t allocated : " + this.getAllocated());
        System.out.println("\t in : " + this.getIn());
        System.out.println("\t out : " + this.getOut());
        System.out.println("\t Cpu used : " + this.getCpuUsed());
    }
}
