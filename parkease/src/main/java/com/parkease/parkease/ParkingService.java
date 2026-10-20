package com.parkease.parkease;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ParkingService {

    private static final int PARKING_CAPACITY = 20;
    private final ParkingRecordRepository repository;

    public ParkingService(ParkingRecordRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ParkingRecord park(String vehicleType, String numberPlate, String ownerName) {
        Vehicle vehicle = VehicleFactory.create(vehicleType, numberPlate, ownerName);
        if (repository.existsByNumberPlateIgnoreCaseAndActiveTrue(numberPlate)) {
            throw new IllegalArgumentException("That vehicle is already parked.");
        }

        boolean[] occupied = getOccupiedSlots();
        int slotsNeeded = vehicle.getVehicleType().getSlotsNeeded();
        int start = findConsecutiveFreeSlots(occupied, slotsNeeded);
        if (start < 0) {
            throw new ParkingFullException("There are not enough consecutive free slots for this vehicle.");
        }

        ParkingRecord record = new ParkingRecord();
        record.setVehicleType(vehicle.getVehicleType().name());
        record.setNumberPlate(vehicle.getNumberPlate());
        record.setOwnerName(vehicle.getOwnerName());
        record.setFirstSlot(start + 1);
        record.setSlotsNeeded(slotsNeeded);
        record.setActive(true);

        ParkingRecord saved = repository.save(record);
        saved.setTicketNumber(String.format("T-%04d", saved.getId() + 1000));
        return repository.save(saved);
    }

    @Transactional(readOnly = true)
    public List<ParkingRecord> getVehicles() {
        return repository.findByActiveTrueOrderByIdAsc();
    }

    @Transactional(readOnly = true)
    public int getOccupiedSlotCount() {
        return getVehicles().stream().mapToInt(ParkingRecord::getSlotsNeeded).sum();
    }

    @Transactional(readOnly = true)
    public List<Integer> getOccupiedSlotNumbers() {
        boolean[] occupied = getOccupiedSlots();
        List<Integer> slots = new ArrayList<>();
        for (int i = 0; i < occupied.length; i++) {
            if (occupied[i]) slots.add(i + 1);
        }
        return slots;
    }

    @Transactional(readOnly = true)
    public List<SlotStatus> getSlotStatuses() {
        List<ParkingRecord> activeVehicles = getVehicles();
        List<SlotStatus> statuses = new ArrayList<>();
        for (int slotNumber = 1; slotNumber <= PARKING_CAPACITY; slotNumber++) {
            ParkingRecord occupant = null;
            for (ParkingRecord vehicle : activeVehicles) {
                int first = vehicle.getFirstSlot();
                int afterLast = first + vehicle.getSlotsNeeded();
                if (slotNumber >= first && slotNumber < afterLast) {
                    occupant = vehicle;
                    break;
                }
            }
            statuses.add(new SlotStatus(slotNumber, occupant != null,
                    occupant == null ? null : occupant.getNumberPlate(),
                    occupant == null ? null : occupant.getVehicleType(),
                    occupant == null ? null : occupant.getTicketNumber()));
        }
        return statuses;
    }

    @Transactional(readOnly = true)
    public long getTotalVehiclesParked() {
        return repository.count();
    }

    @Transactional(readOnly = true)
    public int getTotalCollection() {
        return repository.findAll().stream().mapToInt(ParkingRecord::getCharge).sum();
    }

    @Transactional
    public ExitReceipt exit(String ticketNumber, double hoursParked) {
        if (!Double.isFinite(hoursParked) || hoursParked <= 0) {
            throw new IllegalArgumentException("Hours parked must be a number greater than zero.");
        }

        ParkingRecord record = repository.findByTicketNumberAndActiveTrue(ticketNumber.trim())
                .orElseThrow(() -> new IllegalArgumentException("No active vehicle was found with that ticket number."));

        int billedHours = Math.min(8, (int) Math.ceil(hoursParked));
        Vehicle vehicle = VehicleFactory.from(record);
        Chargeable chargeable = vehicle;
        int hourlyRate = vehicle.getVehicleType().getHourlyRate();
        int charge = chargeable.calculateCharge(billedHours);

        record.setHoursParked(hoursParked);
        record.setBilledHours(billedHours);
        record.setHourlyRate(hourlyRate);
        record.setCharge(charge);
        record.setActive(false);
        repository.save(record);

        return new ExitReceipt(record, hoursParked, billedHours, hourlyRate, charge);
    }

    private boolean[] getOccupiedSlots() {
        boolean[] occupied = new boolean[PARKING_CAPACITY];
        for (ParkingRecord vehicle : repository.findByActiveTrueOrderByIdAsc()) {
            int start = vehicle.getFirstSlot() - 1;
            for (int slot = start; slot < start + vehicle.getSlotsNeeded() && slot < occupied.length; slot++) {
                occupied[slot] = true;
            }
        }
        return occupied;
    }

    private int findConsecutiveFreeSlots(boolean[] occupied, int count) {
        for (int start = 0; start <= occupied.length - count; start++) {
            boolean free = true;
            for (int slot = start; slot < start + count; slot++) {
                if (occupied[slot]) {
                    free = false;
                    break;
                }
            }
            if (free) return start;
        }
        return -1;
    }

    public record ExitReceipt(ParkingRecord ticket, double hoursParked, int billedHours,
                              int hourlyRate, int charge) { }

    public record SlotStatus(int slotNumber, boolean occupied, String numberPlate,
                             String vehicleType, String ticketNumber) { }
}
