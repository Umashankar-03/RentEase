package com.RentEase.service;

import com.RentEase.entity.Location;
import com.RentEase.payload.PagedResponseDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LocationService {
    Location addLocation(Location location);

    PagedResponseDTO<Location> findAllLocation(Pageable pageable);

    boolean findByLocationId(long id);

    void deleteById(long id);

    Location updateLocation(Location location);
}
