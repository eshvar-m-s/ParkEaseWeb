package com.parkease.parkease;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingRecordRepository extends JpaRepository<ParkingRecord, Long> {
    List<ParkingRecord> findByActiveTrueOrderByIdAsc();

    boolean existsByNumberPlateIgnoreCaseAndActiveTrue(String numberPlate);

    Optional<ParkingRecord> findByTicketNumberAndActiveTrue(String ticketNumber);
}
