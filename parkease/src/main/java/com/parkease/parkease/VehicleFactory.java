package com.parkease.parkease;

public final class VehicleFactory {
    private VehicleFactory() { }

    public static Vehicle create(String type, String numberPlate, String ownerName) {
        VehicleType vehicleType = VehicleType.from(type);
        if (!NumberPlateValidator.isValid(numberPlate)) {
            throw new InvalidVehicleException("Enter a valid number plate using 6–12 letters or digits.");
        }
        if (ownerName == null || ownerName.isBlank()) {
            throw new InvalidVehicleException("Enter the vehicle owner's name.");
        }

        return switch (vehicleType) {
            case BIKE -> new Bike(numberPlate, ownerName);
            case CAR -> new Car(numberPlate, ownerName);
            case TRUCK -> new Truck(numberPlate, ownerName);
        };
    }

    public static Vehicle from(ParkingRecord record) {
        return create(record.getVehicleType(), record.getNumberPlate(), record.getOwnerName());
    }
}
