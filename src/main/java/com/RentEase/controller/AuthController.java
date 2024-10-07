package com.RentEase.controller;

import com.RentEase.config.JWTResponseFilter;
import com.RentEase.entity.AppUser;
import com.RentEase.exception.ResourceNotFound;
import com.RentEase.exception.UnauthorizedException;
import com.RentEase.payload.JwtResponse;
import com.RentEase.payload.LoginDto;
import com.RentEase.payload.PagedResponseDTO;
import com.RentEase.payload.UserDto;
import com.RentEase.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }
    //    http://localhost:8080/api/v1/auth/addUser


    // Adding the User
    @PostMapping("/{addUser}")
    public ResponseEntity<Object> addUser(
            @Valid @RequestBody UserDto userDto
    ) {
        UserDto dto = userService.addUser(userDto);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);

    }

    //http://localhost:8080/api/v1/auth/getUser?pageSize=5&pageNo=0&sortBy=id&sortDir=asc
    @GetMapping("/{getUser}")
    public ResponseEntity<PagedResponseDTO<AppUser>> getListUsers(
            @RequestParam(name = "pageSize", defaultValue = "5", required = false) int pageSize,
            @RequestParam(name = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(name = "sortBy", defaultValue = "id" , required = false) String sortBy,
            @RequestParam(name = "sortDir" , defaultValue = "asc" , required = false) String sortDir,
            AppUser user) {
        Sort sortWithDir = (sortDir.equalsIgnoreCase("asc"))?Sort.by(Sort.Direction.ASC, sortBy):Sort.by(Sort.Direction.DESC , sortBy);
       Pageable pageable =  PageRequest.of(pageNo , pageSize , sortWithDir);
        PagedResponseDTO<AppUser> allUser = userService.getAllUser(pageable);
        return new ResponseEntity<>(allUser, HttpStatus.OK);
    }

    @DeleteMapping("/{userDelete}")
    public ResponseEntity<String> deleteUser(@RequestParam long id) {
       return  userService.findById(id)
               .map(user ->{
           userService.deleteUserById(id);
            return new ResponseEntity<>("User is deleted Successful", HttpStatus.OK);
        }).orElseThrow( ()-> new ResourceNotFound("User not found with id:"+ id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUserDetails(@Valid @RequestBody UserDto userDto,   @PathVariable long id) {
         return userService.findById(id)
                 .map(user -> {
                     UserDto dto = userService.updateUserById(id ,userDto);
                     return new ResponseEntity<>(dto, HttpStatus.OK);
                 }).orElseThrow(() -> new ResourceNotFound("User not found with id:"+ id));

    }

// checking the login details with the help of the AppUser table.
@PostMapping("/login")
public ResponseEntity<?>login(@RequestBody LoginDto loginDto){
       String token =  userService.verifyLogin(loginDto);
        if (token !=null){
            JwtResponse response = new JwtResponse();
            response.setToken(token);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            throw new UnauthorizedException("Invalid Credentials");
        }

}

@GetMapping("/profile")
    public AppUser getCurrentProfile(@AuthenticationPrincipal AppUser user){
        return user;
}

}
