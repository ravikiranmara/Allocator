package com.example;

/**
 * Created by wiz on 6/1/18.
 */
public class Component {
    String name;
    int inputPerCpu;
    double inputToOutputRatio;
    boolean congested;
    ComponentState projected;
    ComponentState current;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getInputPerCpu() {
        return inputPerCpu;
    }

    public void setInputPerCpu(int inputPerCpu) {
        this.inputPerCpu = inputPerCpu;
    }

    public double getInputToOutputRatio() {
        return inputToOutputRatio;
    }

    public void setInputToOutputRatio(double inputToOutputRatio) {
        this.inputToOutputRatio = inputToOutputRatio;
    }

    public boolean isCongested() {
        return congested;
    }

    public void setCongested(boolean congested) {
        this.congested = congested;
    }

    public ComponentState getCurrent() {
        return current;
    }

    public void setCurrent(ComponentState current) {
        this.current = current;
    }

    public ComponentState getProjected() {
        return projected;
    }

    public void setProjected(ComponentState projected) {
        this.projected = projected;
    }
}
