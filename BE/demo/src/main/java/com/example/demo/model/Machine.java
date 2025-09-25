// 1. Model - Machine.java
package com.example.demo.model;

public class Machine {
    private String machineCode;
    private String machineName;

    // 기본 생성자
    public Machine() {}

    // 모든 필드를 포함하는 생성자
    public Machine(String machineCode, String machineName) {
        this.machineCode = machineCode;
        this.machineName = machineName;
    }

    // Getter와 Setter
    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    @Override
    public String toString() {
        return "Machine{" +
                "machineCode='" + machineCode + '\'' +
                ", machineName='" + machineName + '\'' +
                '}';
    }
}
