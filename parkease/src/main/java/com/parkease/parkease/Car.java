package com.parkease.parkease;

public class Car extends Vehicle {
    public Car(String numberPlate, String ownerName) {
        super(numberPlate, ownerName, VehicleType.CAR);
    }
}
