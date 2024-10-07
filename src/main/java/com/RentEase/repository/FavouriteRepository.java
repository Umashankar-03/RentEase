package com.RentEase.repository;

import com.RentEase.entity.AppUser;
import com.RentEase.entity.Favourite;
import com.RentEase.entity.Property;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FavouriteRepository extends JpaRepository<Favourite, Long> {

    @Query("select f from Favourite f where f.property = :property and f.appUser = :user")
    Optional<Favourite> checkUserFavouriteProperty(@Param("property") Property property , @Param("user")AppUser user);

    @Query("select f from Favourite f where f.appUser = :user")
    public List<Favourite>getFavourites(@Param("user") AppUser user , Pageable pageable);


    @Modifying
    @Transactional
    @Query("Delete from Favourite f where f.property.id = :propertyId ")
    void deleteByPropertyId(@Param("propertyId") long propertyId);
}