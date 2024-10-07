package com.RentEase.service;

import com.RentEase.entity.Property;
import com.RentEase.payload.PagedResponseDTO;
import org.springframework.data.domain.Pageable;

public interface PropertyService {
    Property addProperty(Property property);

    boolean deleteProperty(long id);

    Property updateProperty(long id, Property property);


    PagedResponseDTO<Property> getPropertyListByLocation(String locationName, Pageable pageable);

     void deletePropertyById(long id);

    Property getPropertyById(long propertyId);

    PagedResponseDTO<Property> getAllProperties(int pageSize, int pageNo, String sortBy, String sortDir);
}
