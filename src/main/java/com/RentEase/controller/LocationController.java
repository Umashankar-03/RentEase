package com.RentEase.controller;

import com.RentEase.entity.Location;
import com.RentEase.payload.PagedResponseDTO;
import com.RentEase.service.LocationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/location")
public class LocationController {

    private LocationService locationService;


    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping
    public ResponseEntity<Location>addLocation(@Valid @RequestBody Location location){
        Location addedLocation = locationService.addLocation(location);
        return new ResponseEntity<>(addedLocation, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PagedResponseDTO<Location>>getAllLocation(
            @RequestParam(name = "pageSize" , defaultValue = "5" , required = false) int pageSize,
            @RequestParam(name = "pageNo" , defaultValue = "0", required = false) int pageNo,
            @RequestParam(name = "sortBy" , defaultValue = "locationName" , required = false) String sortBy,
            @RequestParam(name = "sortDir" , defaultValue = "asc" , required = false) String sortDir
    ){
        Pageable pageable = null;
        if(sortDir.equalsIgnoreCase("asc")){
            pageable = PageRequest.of(pageNo,pageSize,Sort.by(sortBy).ascending());
        } else if (sortDir.equalsIgnoreCase("desc")) {
            pageable = PageRequest.of(pageNo,pageSize,Sort.by(sortBy).descending());
        }
        PagedResponseDTO<Location> allLocation = locationService.findAllLocation(pageable);
        return new ResponseEntity<>(allLocation,HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<String>deleteLocation(@RequestParam long id){
        boolean status = locationService.findByLocationId(id);
        if (status){
            locationService.deleteById(id);
            return new ResponseEntity<>("Location Deleted",HttpStatus.OK);
        }
        return new ResponseEntity<>("Invalid ", HttpStatus.BAD_REQUEST);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Object>updateLocation(@PathVariable long id , @Valid @RequestBody Location location){
        boolean status = locationService.findByLocationId(id);
        if(status){
            location.setId(id);
            Location updatedLocation = locationService.updateLocation(location);
            return new ResponseEntity<>(updatedLocation,HttpStatus.OK);
        }
        return new ResponseEntity<>("Invalid ",HttpStatus.BAD_REQUEST);
    }


}
