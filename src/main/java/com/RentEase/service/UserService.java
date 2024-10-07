package com.RentEase.service;

import com.RentEase.entity.AppUser;
import com.RentEase.payload.LoginDto;
import com.RentEase.payload.PagedResponseDTO;
import com.RentEase.payload.UserDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {
    public UserDto addUser(UserDto userDto);



  public  void deleteUserById(long id);

   Optional<AppUser> findById(long id);

   public UserDto updateUserById(long id, UserDto userDto);


   public String verifyLogin(LoginDto loginDto);

    PagedResponseDTO<AppUser> getAllUser(Pageable pageable);
}
