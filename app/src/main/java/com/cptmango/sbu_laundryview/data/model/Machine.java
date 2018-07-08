package com.cptmango.sbu_laundryview.data.model;

public class Machine {

    private int machineNumber;
    private int timeLeft;
    private MachineStatus status;
    private boolean favorite = false;
    private boolean isWasher = false;

    public Machine(int machineNumber, int timeLeft, MachineStatus status, boolean isWasher){

        this.machineNumber = machineNumber;
        this.timeLeft = timeLeft;
        this.status = status;
        this.isWasher = isWasher;

    }

    public int machineNumber(){ return machineNumber; }

    public void setMachineNumber(int number) { this.machineNumber = number; }

    public int timeLeft(){ return timeLeft; }
    public void setTimeLeft(int timeLeft){ this.timeLeft = timeLeft; }

    public MachineStatus machineStatus() { return status; }

    public boolean isFavorite(){ return favorite; }
    public void setFavorite(boolean favorite){ this.favorite = favorite; }

    public boolean isWasher(){ return isWasher; }
    public void setIsWasher(boolean isWasher){ this.isWasher = isWasher; }

}
