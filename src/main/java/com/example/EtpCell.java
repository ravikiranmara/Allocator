package com.example;

public class EtpCell {
    private Double etp;
    private String component;

    public EtpCell (String component, Double etp) {
        this.component = component;
        this.etp = etp;
    }

    public void setEtp(Double etp) {
        this.etp = etp;
    }

    public Double getEtp() {
        return this.etp;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getComponent() {
        return component;
    }
    public void dump() {
        System.out.println("Component : " + component);
        System.out.println("Etp : " + etp);
    }
}
