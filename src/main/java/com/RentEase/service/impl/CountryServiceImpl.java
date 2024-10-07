package com.RentEase.service.impl;

import com.RentEase.entity.Country;
import com.RentEase.payload.CountryDto;
import com.RentEase.payload.PagedResponseDTO;
import com.RentEase.repository.CountryRepository;
import com.RentEase.service.CountryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CountryServiceImpl implements CountryService {

    private CountryRepository countryRepository;
    private final ModelMapper modelMapper;


    public CountryServiceImpl(CountryRepository countryRepository,
                              ModelMapper modelMapper) {
        this.countryRepository = countryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CountryDto addCountry(CountryDto countryDto) {

        Country country = mapDtoToCountry(countryDto);
        Country savedcountry = countryRepository.save(country);
        CountryDto dto = mapCountryToDto(savedcountry);
        return dto;
    }


    @Override
    public PagedResponseDTO<CountryDto> getAllCountryList(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort =   (sortDir.equalsIgnoreCase("asc"))? Sort.by(Sort.Direction.ASC, sortBy):Sort.by(Sort.Direction.DESC, sortBy);
        Pageable pageable =  PageRequest.of(pageNo , pageSize , sort);
        Page<Country> pageResult = countryRepository.findAll(pageable);

        return mapPageResponseToPagedResponseDTO(pageResult);

//        List<CountryDto> countryDto = pageResult.getContent()
//                .stream()
//                .map(country -> mapCountryToDto(country))
//                .toList();
//
//        PagedResponseDTO<CountryDto> response = new PagedResponseDTO<CountryDto>();
//
//        response.setContent(countryDto);
//        response.setPageSize(pageResult.getSize());
//        response.setPageNumber(pageResult.getNumber());
//        response.setTotalPages(pageResult.getTotalPages());
//        response.setTotalElements(pageResult.getTotalElements());
//        response.setHasPrevious(pageResult.hasPrevious());
//        response.setHasNext(pageResult.hasNext());
//        response.setFirst(pageResult.isFirst());
//        response.setLast(pageResult.isLast());
//        return  response;

    }


    @Override
    public boolean deleteCountry(long id) {
        Optional<Country> byId = countryRepository.findById(id);
        if (byId.isPresent()){
            countryRepository.deleteById(id);
            return true;
    }
    return false;
    }

    @Override
    public boolean findCountryById(long id) {
        Optional<Country> byId = countryRepository.findById(id);
        return byId.isPresent();
    }

    @Override
    public Country updateCountry(Country country) {
        Country saved = countryRepository.save(country);
        return saved;
    }

    public PagedResponseDTO<CountryDto>mapPageResponseToPagedResponseDTO(Page<Country> pageResult){

        PagedResponseDTO<CountryDto> response = new PagedResponseDTO<CountryDto>();

        List<CountryDto> countryDto = pageResult.getContent()
                .stream()
                .map(country -> mapCountryToDto(country))
                .toList();

        response.setContent(countryDto);
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


public Country mapDtoToCountry(CountryDto countryDto){
        Country country = modelMapper.map(countryDto, Country.class);
        return  country;
}

public CountryDto mapCountryToDto(Country country){
        CountryDto  countryDto = modelMapper.map(country, CountryDto.class);
        return countryDto;
}



}
