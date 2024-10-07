package com.RentEase.controller;

import com.RentEase.entity.Country;
import com.RentEase.payload.CountryDto;
import com.RentEase.payload.PagedResponseDTO;
import com.RentEase.service.CountryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/countries")
public class CountryController {

    private final CountryService countryService;

    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    // http://localhost:8080/api/v1/countries/addCountry
    @PostMapping("/addCountry")
    public ResponseEntity<CountryDto> addCountry(@Valid @RequestBody CountryDto countryDto) {
        CountryDto countryDto1 = countryService.addCountry(countryDto);

        return new ResponseEntity<>(countryDto1, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PagedResponseDTO <CountryDto>> getCountryList(
            @RequestParam(name = "pageSize", defaultValue = "5", required = false) int pageSize,
            @RequestParam(name = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(name = "sortBy", defaultValue = "countryName", required = false) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "asc", required = false) String sortDir) {

        PagedResponseDTO<CountryDto> allCountryList = countryService.getAllCountryList(pageNo, pageSize, sortBy, sortDir);
        return  new ResponseEntity<>(allCountryList , HttpStatus.OK);
    }


    @DeleteMapping
    public ResponseEntity<String> deleteCountry(@RequestParam long id) {
        boolean status = countryService.findCountryById(id);
        if (status) {
            countryService.deleteCountry(id);
            return new ResponseEntity<>("Country is deleted", HttpStatus.OK);
        }
        return new ResponseEntity<>("invalid country id", HttpStatus.BAD_REQUEST);
    }

    @PutMapping
    public ResponseEntity<?> updateCountry(@Valid @RequestParam long id, @RequestBody Country country) {
        boolean countryById = countryService.findCountryById(id);
        if (countryById) {
            country.setId(id);
            Country updatedCountry = countryService.updateCountry(country);
            return new ResponseEntity<>(updatedCountry, HttpStatus.OK);
        }
        return new ResponseEntity<>("Check the id ", HttpStatus.BAD_REQUEST);
    }

}

