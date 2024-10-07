package com.RentEase.repository;

import com.RentEase.entity.AppUser;
import com.RentEase.entity.Booking;
import com.RentEase.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByPropertyAndCheckInDateBeforeAndCheckOutDateAfter(Property property, LocalDate checkInDate, LocalDate checkOutDate);

//    @Query("SELECT b FROM Booking b WHERE b.property = :property " +
//            "AND b.checkInDate < :checkOutDate " +
//            "AND b.checkOutDate > :checkInDate")
//    Optional<Booking> checkOverlappingBooking(
//            @Param("property") Property property,
//            @Param("checkInDate") LocalDate checkInDate,
//            @Param("checkOutDate") LocalDate checkOutDate );

   boolean existsByAppUserAndPropertyAndCheckInDate(AppUser user , Property property , LocalDate checkInDate);

}