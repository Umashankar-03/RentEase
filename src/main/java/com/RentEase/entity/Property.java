package com.RentEase.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "property")
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotEmpty
    @Size(min = 2 ,message = "Property name must be minimum 2 characters")
    @Column(name = "property_name", nullable = false)
    private String propertyName;

    @NotNull(message = "Number of guest is required")
    @Column(name = "guest")
    private Integer guest;

    @NotNull(message = "Number of beds is required")
    @Column(name = "beds", nullable = false)
    private Integer beds;

    @NotNull(message = "Number of bathrooms is required")
    @Column(name = "bathrooms", nullable = false)
    private Integer bathrooms;

    @NotNull(message = "Number of bedrooms is required")
    @Column(name = "bedrooms", nullable = false)
    private Integer bedrooms;

    @NotNull(message = "Nightly price is required")
    @Column(name = "nightly_price", nullable = false)
    private Integer nightlyPrice;

    @NotNull(message = "Location is required")
    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @NotNull(message = "Country is required")
    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;


//    @JsonIgnore
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Favourite> favourites = new LinkedHashSet<>();



}