package com.RentEase.service;

import com.RentEase.entity.Country;
import com.RentEase.payload.CountryDto;
import com.RentEase.payload.PagedResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;


public interface CountryService {


    CountryDto addCountry(CountryDto countryDto);

   

    boolean deleteCountry(long id);

    boolean findCountryById(long id);

    Country updateCountry(Country country);

    PagedResponseDTO<CountryDto> getAllCountryList(int pageNo, int pageSize, String sortBy, String sortDir);


//    List<Country> updateCountry(long id);
}
