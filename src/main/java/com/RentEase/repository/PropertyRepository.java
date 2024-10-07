package com.RentEase.repository;

import com.RentEase.entity.Property;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository

public interface PropertyRepository extends JpaRepository<Property, Long> {

    // Logger logger= LoggerFactory.getLogger(PropertyRepository.class);
    @Query("select p from Property p join Location l on p.location = l.id join Country c on p.country = c.id where l.locationName =:locationName or c.countryName=:locationName")
    Page<Property> listPropertyByLocationOrCountryName(@Param("locationName") String locationName , Pageable pageable);




}