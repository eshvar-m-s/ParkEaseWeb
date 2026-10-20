package com.parkease.parkease;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "parking_record")
public class ParkingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String ticketNumber;

    @Column(nullable = false, length = 10)
    private String vehicleType;

    @Column(nullable = false, length = 12)
    private String numberPlate;

    @Column(nullable = false, length = 100)
    private String ownerName;

    private int firstSlot;
    private int slotsNeeded;
    private boolean active = true;
    private double hoursParked;
    private int billedHours;
    private int hourlyRate;
    private int charge;

    protected ParkingRecord() {
        // Required by JPA.
    }

    public Long getId() { return id; }
    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }
    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    public String getNumberPlate() { return numberPlate; }
    public void setNumberPlate(String numberPlate) { this.numberPlate = numberPlate; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public int getFirstSlot() { return firstSlot; }
    public void setFirstSlot(int firstSlot) { this.firstSlot = firstSlot; }
    public int getSlotsNeeded() { return slotsNeeded; }
    public void setSlotsNeeded(int slotsNeeded) { this.slotsNeeded = slotsNeeded; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public double getHoursParked() { return hoursParked; }
    public void setHoursParked(double hoursParked) { this.hoursParked = hoursParked; }
    public int getBilledHours() { return billedHours; }
    public void setBilledHours(int billedHours) { this.billedHours = billedHours; }
    public int getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(int hourlyRate) { this.hourlyRate = hourlyRate; }
    public int getCharge() { return charge; }
    public void setCharge(int charge) { this.charge = charge; }
}
