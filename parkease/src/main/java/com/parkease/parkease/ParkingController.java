package com.parkease.parkease;

import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ParkingController {

    private static final int PARKING_CAPACITY = 20;
    private final ParkingService parkingService;

    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @GetMapping("/")
    public String showHomePage(Model model) {
        List<Integer> slotNumbers = IntStream.rangeClosed(1, PARKING_CAPACITY)
                .boxed()
                .toList();

        model.addAttribute("totalSlots", PARKING_CAPACITY);
        model.addAttribute("occupiedSlots", parkingService.getOccupiedSlotCount());
        model.addAttribute("availableSlots", PARKING_CAPACITY - parkingService.getOccupiedSlotCount());
        model.addAttribute("totalCollection", "Rs. " + parkingService.getTotalCollection());
        model.addAttribute("slotNumbers", slotNumbers);
        model.addAttribute("occupiedSlotNumbers", parkingService.getOccupiedSlotNumbers());
        model.addAttribute("parkedVehicles", parkingService.getVehicles());

        return "index";
    }

    @GetMapping("/park")
    public String showParkVehicleForm() {
        return "park";
    }

    @PostMapping("/park")
    public String parkVehicle(@RequestParam String vehicleType,
                              @RequestParam String numberPlate,
                              @RequestParam String ownerName,
                              Model model) {
        String cleanPlate = numberPlate.trim().toUpperCase();
        String cleanOwner = ownerName.trim();

        try {
            ParkingRecord ticket = parkingService.park(vehicleType, cleanPlate, cleanOwner);
            model.addAttribute("ticket", ticket);
            return "ticket";
        } catch (InvalidVehicleException | ParkingFullException | IllegalArgumentException exception) {
            model.addAttribute("errorMessage", exception.getMessage());
            model.addAttribute("vehicleType", vehicleType);
            model.addAttribute("numberPlate", cleanPlate);
            model.addAttribute("ownerName", cleanOwner);
            return "park";
        }
    }

    @GetMapping("/exit")
    public String showExitForm(Model model) {
        model.addAttribute("activeTickets", parkingService.getVehicles());
        return "exit";
    }

    @GetMapping("/summary")
    public String showSummary(Model model) {
        model.addAttribute("totalVehiclesParked", parkingService.getTotalVehiclesParked());
        model.addAttribute("activeVehicleCount", parkingService.getVehicles().size());
        model.addAttribute("occupiedSlotCount", parkingService.getOccupiedSlotCount());
        model.addAttribute("totalCollection", parkingService.getTotalCollection());
        model.addAttribute("activeTickets", parkingService.getVehicles());
        return "summary";
    }

    @GetMapping("/slots")
    public String showSlotMap(Model model) {
        model.addAttribute("slotStatuses", parkingService.getSlotStatuses());
        model.addAttribute("occupiedSlots", parkingService.getOccupiedSlotCount());
        model.addAttribute("availableSlots", PARKING_CAPACITY - parkingService.getOccupiedSlotCount());
        return "slots";
    }

    @PostMapping("/exit")
    public String exitVehicle(@RequestParam String ticketNumber,
                              @RequestParam String hoursParked,
                              Model model) {
        try {
            double parsedHours = Double.parseDouble(hoursParked);
            model.addAttribute("receipt", parkingService.exit(ticketNumber, parsedHours));
            return "exit-receipt";
        } catch (NumberFormatException exception) {
            model.addAttribute("errorMessage", "Enter the parked time as a number greater than zero.");
            model.addAttribute("ticketNumber", ticketNumber);
            model.addAttribute("hoursParked", hoursParked);
            model.addAttribute("activeTickets", parkingService.getVehicles());
            return "exit";
        } catch (IllegalArgumentException exception) {
            model.addAttribute("errorMessage", exception.getMessage());
            model.addAttribute("ticketNumber", ticketNumber);
            model.addAttribute("hoursParked", hoursParked);
            model.addAttribute("activeTickets", parkingService.getVehicles());
            return "exit";
        }
    }
}
