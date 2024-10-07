package com.RentEase.service.impl;

import com.RentEase.entity.Country;
import com.RentEase.entity.Location;
import com.RentEase.entity.Property;
import com.RentEase.exception.ResourceNotFound;
import com.RentEase.payload.PagedResponseDTO;
import com.RentEase.repository.CountryRepository;
import com.RentEase.repository.FavouriteRepository;
import com.RentEase.repository.LocationRepository;
import com.RentEase.repository.PropertyRepository;
import com.RentEase.service.PropertyService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PropertyServiceImpl implements PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private LocationRepository locationRepository;

    private FavouriteRepository favouriteRepository;

    public PropertyServiceImpl(PropertyRepository propertyRepository, CountryRepository countryRepository, LocationRepository locationRepository, FavouriteRepository favouriteRepository) {
        this.propertyRepository = propertyRepository;
        this.countryRepository = countryRepository;
        this.locationRepository = locationRepository;
        this.favouriteRepository = favouriteRepository;
    }

    @Override
    public Property addProperty(Property property) {
        Location location = locationRepository.findById(property.getLocation().getId())
                .orElseThrow(() -> new RuntimeException("Location not found"));
        Country country = countryRepository.findById(property.getCountry().getId())
                .orElseThrow(() -> new RuntimeException("Country Not found"));
        property.setLocation(location);
        property.setCountry(country);
        Property saved = propertyRepository.save(property);

        return saved;
    }

    @Override
    public boolean deleteProperty(long id) {
        Optional<Property> byId = propertyRepository.findById(id);
        if (byId.isPresent()){
            propertyRepository.deleteById(id);
        }else {
            throw new RuntimeException("Property Not found");
        }
        return false;
    }

    @Override
    public Property updateProperty(long id, Property propertyDetails) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        Location location = locationRepository.findById(propertyDetails.getLocation().getId())
                .orElseThrow(() -> new RuntimeException("Property not found"));
        Country country = countryRepository.findById(propertyDetails.getCountry().getId())
                .orElseThrow(() -> new RuntimeException("Property not found"));

        property.setPropertyName(propertyDetails.getPropertyName());
        property.setGuest(propertyDetails.getGuest());
        property.setBeds(propertyDetails.getBeds());
        property.setBathrooms(propertyDetails.getBathrooms());
        property.setBedrooms(propertyDetails.getBedrooms());
        property.setNightlyPrice(propertyDetails.getNightlyPrice());
        property.setLocation(location);
        property.setCountry(country);

        Property saved = propertyRepository.save(property);

        return saved;
    }

    @Override
    public PagedResponseDTO<Property> getPropertyListByLocation(String locationName, Pageable pageable) {

        Page<Property> pageResult = propertyRepository.listPropertyByLocationOrCountryName(locationName ,pageable);

        return mapPageResultToResponse(pageResult);
    }


         @Override
         @Transactional
        public void deletePropertyById(long id) {
            Property property = propertyRepository.findById(id)
                    .orElseThrow(
                            () -> new ResourceNotFound("Property not found with id: " + id)
                    );

//            favouriteRepository.deleteByPropertyId(property.getId());
            propertyRepository.deleteById(id);

        }

    @Override
    public Property getPropertyById(long propertyId) {
         return  propertyRepository.findById(propertyId).orElseThrow(
                () -> new ResourceNotFound(" Property not found with id : " + propertyId)
                );


    }

    @Override
    public PagedResponseDTO<Property> getAllProperties(int pageSize, int pageNo, String sortBy, String sortDir) {
        Sort sortWithDir = (sortDir.equalsIgnoreCase("asc")) ? Sort.by(Sort.Direction.ASC, sortBy) : Sort.by(Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(pageNo, pageSize, sortWithDir);
        Page<Property> pageResult = propertyRepository.findAll(pageable);
        if (pageResult.isEmpty()){
            throw new ResourceNotFound("No properties found");
        }
        return mapPageResultToResponse(pageResult);
    }


    public PagedResponseDTO<Property>mapPageResultToResponse(Page<Property> pageResult){
        PagedResponseDTO<Property> response = new PagedResponseDTO<>();
        response.setContent(pageResult.getContent());
        response.setPageNumber(pageResult.getNumber());
        response.setPageSize(pageResult.getSize());
        response.setTotalPages(pageResult.getTotalPages());
        response.setTotalElements(pageResult.getTotalElements());
        response.setHasNext(pageResult.hasNext());
        response.setHasPrevious(pageResult.hasPrevious());
        response.setFirst(pageResult.isFirst());
        response.setLast(pageResult.isLast());
        return  response;
    }
}