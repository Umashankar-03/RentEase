package com.RentEase.payload;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CountryDto {

    private long id;

    @NotEmpty
    @Size(min = 2 ,message = "Country name must be minimum 2 characters")
    private String countryName;

   public long getId(){
       return id;
   }
   public void setId(long id){
       this.id = id;
   }
   public String getCountryName(){
       return countryName;
   }

   public void setCountryName(){
       this.countryName = countryName;
   }
}
