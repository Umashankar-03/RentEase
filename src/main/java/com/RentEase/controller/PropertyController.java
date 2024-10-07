package com.RentEase.controller;

import com.RentEase.entity.Property;
import com.RentEase.exception.ResourceNotFound;
import com.RentEase.payload.PagedResponseDTO;
import com.RentEase.repository.PropertyRepository;
import com.RentEase.service.PropertyService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/property")
public class PropertyController {

    private PropertyRepository propertyRepository;
    private PropertyService propertyService;

    public PropertyController(PropertyRepository propertyRepository, PropertyService propertyService) {
        this.propertyRepository = propertyRepository;
        this.propertyService = propertyService;
    }


    @PostMapping
    public ResponseEntity<Property>addProperty(@Valid @RequestBody Property property){
        Property addedProperty = propertyService.addProperty(property);
        return new ResponseEntity<>(addedProperty,HttpStatus.OK);
    }


    @GetMapping("/{locationName}")
    public ResponseEntity<PagedResponseDTO<Property>> getPropertyLitings(
            @RequestParam(name = "pageSize" , defaultValue = "5" , required = false) int pageSize,
            @RequestParam(name = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(name = "sortBy" , defaultValue = "id" , required = false) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "asc" , required = false) String sortDir,
            @PathVariable String locationName
    ){
       Sort sortWithDir =  (sortDir.equalsIgnoreCase("asc"))?Sort.by(Sort.Direction.ASC,sortBy):Sort.by(Sort.Direction.DESC,sortBy);
        Pageable pageable = PageRequest.of(pageNo, pageSize, sortWithDir);
       PagedResponseDTO <Property> proprertyListByLocation = propertyService.getPropertyListByLocation(locationName ,pageable);
        return  new ResponseEntity<>(proprertyListByLocation , HttpStatus.OK);
    }


    @GetMapping("/id/{propertyId}")
    public ResponseEntity<Property>getPropertyById(
            @PathVariable long propertyId
    ){
        Property propertyById = propertyService.getPropertyById(propertyId);
        return ResponseEntity.ok(propertyById);
    }


    //http://localhost:8080/api/v1/property?pageSize=5&pageNo=0&sortBy=nightlyPrice&sortDir=asc
    @GetMapping
    public ResponseEntity<PagedResponseDTO<Property>>getAllProperties(
        @RequestParam(name = "pageSize" , defaultValue = "5" , required = false) int pageSize,
        @RequestParam(name = "pageNo" , defaultValue = "o" , required = false) int pageNo,
        @RequestParam(name = "sortBy" , defaultValue = "id" , required = false) String sortBy ,
        @RequestParam(name = "sortDir" , defaultValue = "asc" , required = false) String sortDir
    ){
        PagedResponseDTO<Property>allProperties = propertyService.getAllProperties(pageSize, pageNo, sortBy, sortDir);
        return new ResponseEntity<>(allProperties,HttpStatus.OK);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Property>updateProperty(@PathVariable long id ,@Valid @RequestBody Property propertyDetails){
        Property updatedProperty = propertyService.updateProperty(id, propertyDetails);
        return new ResponseEntity<>(updatedProperty,HttpStatus.OK);
    }


    @DeleteMapping
    public ResponseEntity<String>deleteProperty(@RequestParam long id){
        propertyService.deletePropertyById(id);
        return new ResponseEntity<>("Property deleted successfully.", HttpStatus.OK);
    }


}












