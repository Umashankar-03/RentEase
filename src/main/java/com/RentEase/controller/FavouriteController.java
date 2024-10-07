package com.RentEase.controller;

import com.RentEase.entity.AppUser;
import com.RentEase.entity.Favourite;
import com.RentEase.entity.Property;
import com.RentEase.exception.ResourceNotFound;
import com.RentEase.repository.FavouriteRepository;
import com.RentEase.repository.PropertyRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/favourite")
public class FavouriteController {

    private FavouriteRepository favouriteRepository;

    private PropertyRepository propertyRepository;

    public FavouriteController(FavouriteRepository favouriteRepository, PropertyRepository propertyRepository) {
        this.favouriteRepository = favouriteRepository;
        this.propertyRepository = propertyRepository;
    }

    @PostMapping("/addFavourite")
    public ResponseEntity<Object>addFavourite(
            @AuthenticationPrincipal AppUser user,
            @RequestParam long propertyId
            ) {
    Property property =  propertyRepository.findById(propertyId).orElseThrow(
                 ()->  new ResourceNotFound("Property with ID " + propertyId + "not found"));
        Favourite existingFavourite = favouriteRepository.checkUserFavouriteProperty(property, user)
                .orElse(null);

        if (existingFavourite == null ) {
            Favourite favourite = new Favourite();
            favourite.setAppUser(user);
            favourite.setProperty(property);
            favourite.setIsFavourite(true);

            Favourite savedFavourite = favouriteRepository.save(favourite);
            return new ResponseEntity<>(savedFavourite, HttpStatus.CREATED);
        }
                favouriteRepository.delete(existingFavourite);
                return new ResponseEntity<>("favourite is removed", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/userFavouriteList")
    public ResponseEntity<Object>getAllFavouritesOfUser(
            @RequestParam(name =  "pageSize", defaultValue = "5" , required = false) int pageSize,
            @RequestParam(name =  "pageNo", defaultValue = "0" , required = false) int pageNo,
            @AuthenticationPrincipal AppUser user
    ){
        Pageable pageable = PageRequest.of(pageNo,pageSize);
        List<Favourite> favourites = favouriteRepository.getFavourites(user, pageable);
        if(favourites.isEmpty()){
            return new ResponseEntity<>("No favourite properties found for the user ",HttpStatus.OK);
        }else {
            return new ResponseEntity<>(favourites,HttpStatus.OK);
        }

    }

}