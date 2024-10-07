package com.RentEase.controller;

import com.RentEase.entity.AppUser;
import com.RentEase.entity.Property;
import com.RentEase.entity.Review;
import com.RentEase.repository.PropertyRepository;
import com.RentEase.repository.ReviewRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private ReviewRepository reviewRepository;
    private PropertyRepository propertyRepository;

    public ReviewController(ReviewRepository reviewRepository, PropertyRepository propertyRepository) {
        this.reviewRepository = reviewRepository;

        this.propertyRepository = propertyRepository;
    }

    @PostMapping
    public ResponseEntity<String>addReview(
            @AuthenticationPrincipal AppUser user,
            @RequestParam long propertyId,
            @RequestBody Review review
            ){
        Optional<Property> byId = propertyRepository.findById(propertyId);
        if (!byId.isPresent()) {
            return new ResponseEntity<>("Property not found", HttpStatus.NOT_FOUND);
        }
        Property property = byId.get();
        Review fetched = reviewRepository.fetchUserReview(property, user);
        if (fetched != null) {
            return new ResponseEntity<>("Review is already given ", HttpStatus.BAD_REQUEST);
        }
            review.setAppUser(user);
            review.setProperty(property);

            reviewRepository.save(review);
            return new ResponseEntity<>("Review added ", HttpStatus.CREATED);

    }

    @GetMapping
    public ResponseEntity<List<Review>>getReview(@RequestParam long propertyId){
        Optional<Property> property = propertyRepository.findById(propertyId);
        if (property.isPresent()) {
            Property prop = property.get();
            List<Review> reviews = reviewRepository.findByProperty(prop);
            return new ResponseEntity<>(reviews, HttpStatus.OK);
        }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping
    public ResponseEntity<String>deleteReview(
            @RequestParam long id ,
            @AuthenticationPrincipal AppUser user
    ) {
        Optional<Review> opReview = reviewRepository.findById(id);
        if (opReview.isPresent()) {
            Review review = opReview.get();

            if (review.getAppUser().getId().equals(user.getId())) {
                reviewRepository.deleteById(id);
                return new ResponseEntity<>("Review deleted successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("You are not authorized to delete this review", HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>("Review not Found", HttpStatus.NOT_FOUND);
           
        }

    }
}
