package com.RentEase.repository;

import com.RentEase.entity.AppUser;
import com.RentEase.entity.Property;
import com.RentEase.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("select r from Review r where r.property = :property and r.appUser = :user")
    Review fetchUserReview(@Param("property")Property property , @Param("user") AppUser user);

    List<Review> findByProperty(Property prop);
}