package com.parkease.parkease;

public abstract class Vehicle implements Chargeable {
    private final String numberPlate;
    private final String ownerName;
    private final VehicleType vehicleType;

    protected Vehicle(String numberPlate, String ownerName, VehicleType vehicleType) {
        this.numberPlate = numberPlate;
        this.ownerName = ownerName;
        this.vehicleType = vehicleType;
    }

    public String getNumberPlate() { return numberPlate; }
    public String getOwnerName() { return ownerName; }
    public VehicleType getVehicleType() { return vehicleType; }

    @Override
    public int calculateCharge(int hours) {
        return hours * vehicleType.getHourlyRate();
    }
}
