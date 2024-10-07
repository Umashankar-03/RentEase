package com.RentEase.service.impl;

import com.RentEase.entity.AppUser;
import com.RentEase.entity.Location;
import com.RentEase.payload.PagedResponseDTO;
import com.RentEase.repository.LocationRepository;
import com.RentEase.service.LocationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocationServiceImpl implements LocationService {

    private LocationRepository locationRepository;

    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public Location addLocation(Location location) {
        Location saved = locationRepository.save(location);
        return saved;
    }

    @Override
    public PagedResponseDTO<Location> findAllLocation(Pageable pageable) {
        Page<Location> pageResult = locationRepository.findAll(pageable);
      return mapPageResponseToPagedResponseDTO(pageResult);
    }

    @Override
    public boolean findByLocationId(long id) {
        Optional<Location> byId = locationRepository.findById(id);
        if (byId.isPresent()){
            return true;
        }
        return false;
    }

    @Override
    public void deleteById(long id) {
        locationRepository.deleteById(id);
    }

    @Override
    public Location updateLocation(Location location) {
        Location saved = locationRepository.save(location);
        return saved;
    }

    PagedResponseDTO<Location>mapPageResponseToPagedResponseDTO(Page<Location>pageResult){
        PagedResponseDTO<Location> response = new PagedResponseDTO<>();
        response.setContent(pageResult.getContent());
        response.setPageSize(pageResult.getSize());
        response.setPageNumber(pageResult.getNumber());
        response.setTotalPages(pageResult.getTotalPages());
        response.setTotalElements(pageResult.getTotalElements());
        response.setHasPrevious(pageResult.hasPrevious());
        response.setHasNext(pageResult.hasNext());
        response.setFirst(pageResult.isFirst());
        response.setLast(pageResult.isLast());
        return  response;
    }
}
