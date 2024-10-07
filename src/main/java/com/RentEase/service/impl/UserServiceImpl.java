package com.RentEase.service.impl;

import com.RentEase.entity.AppUser;
import com.RentEase.exception.DuplicateResourceException;
import com.RentEase.payload.LoginDto;
import com.RentEase.payload.PagedResponseDTO;
import com.RentEase.payload.UserDto;
import com.RentEase.repository.AppUserRepository;
import com.RentEase.service.JWTService;
import com.RentEase.service.UserService;
import com.twilio.rest.microvisor.v1.App;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service

public class UserServiceImpl implements UserService {

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private JWTService jwtService;

    public UserServiceImpl(AppUserRepository userRepository, JWTService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Override
    public UserDto addUser(UserDto userDto) {
     userRepository.findByUsernameOrEmailId(userDto.getUsername(), userDto.getEmailId())
             .ifPresent(existingUser -> {
                 throw new DuplicateResourceException("Username or Email ID already exists: " + userDto.getUsername() + " Or " + userDto.getEmailId() );
             });
        AppUser user = mapToEntity(userDto);
        AppUser saved = userRepository.save(user);
        UserDto dto = mapToDto(saved);
        return dto;
    }

    @Override
    public PagedResponseDTO<AppUser>getAllUser(Pageable pageable) {
        Page<AppUser> pageResult = userRepository.findAll(pageable);
        return  mapPageResponseToPagedResponseDTO(pageResult);
    }

    @Override
    public void deleteUserById(long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Optional<AppUser>  findById(long id) {
     return userRepository.findById(id);

    }

    @Override
    public UserDto updateUserById(long id, UserDto userDto) {
        return userRepository.findById(id)
                .map( existingUser -> {
                    existingUser.setName(userDto.getName());
                    existingUser.setUsername(userDto.getUsername());
                    existingUser.setEmailId(userDto.getEmailId());
//            existingUser.setUserRole(userDto.getUserRole());
//            existingUser.setPassword(userDto.getPassword());
                    if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
                        existingUser.setPassword(BCrypt.hashpw(userDto.getPassword(), BCrypt.gensalt(10)));
                    }

                    if (userDto.getUserRole() != null && !userDto.getUserRole().isEmpty()) {
                        existingUser.setUserRole(userDto.getUserRole());
                    }
                    AppUser saved = userRepository.save(existingUser);
                    UserDto dto = mapToDto(saved);
                    return dto;

        }).orElseThrow(null);
}
//        AppUser appUser = mapToEntity(userDto);
//        AppUser saved = userRepository.save(appUser);
//        UserDto dto = mapToDto(saved);
//        return dto;


    @Override
    public String verifyLogin(LoginDto loginDto) {
        Optional<AppUser> opUser = userRepository.findByUsernameOrEmailId(loginDto.getUsername(), loginDto.getEmailId());

        if(opUser.isPresent()){
            AppUser user = opUser.get();
           if (BCrypt.checkpw(loginDto.getPassword(),user.getPassword())) {
                return jwtService.generateToken(user);
            }
        }
        return null;
    }


    //Convert the  Dto to Entity t
    AppUser mapToEntity(UserDto userDto) {
        AppUser user = new AppUser();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setUsername(userDto.getUsername());
        user.setEmailId(userDto.getEmailId());
        user.setUserRole("ROLE_USER");
//        user.setPassword( new BCryptPasswordEncoder().encode( userDto.getPassword()));
        user.setPassword(BCrypt.hashpw(userDto.getPassword(), BCrypt.gensalt(10)));
        return user;
    }

    //Convert to Entity to Dto
    UserDto mapToDto(AppUser user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setUsername(user.getUsername());
        dto.setEmailId(user.getEmailId());
        dto.setUserRole(user.getUserRole());
        dto.setPassword(user.getPassword());
        return dto;
    }


    public   PagedResponseDTO<AppUser> mapPageResponseToPagedResponseDTO( Page<AppUser> pageResult){

        PagedResponseDTO<AppUser> response = new PagedResponseDTO<AppUser>();
        response.setContent(pageResult.getContent());
        response.setPageNumber(pageResult.getNumber());
        response.setPageSize(pageResult.getSize());
        response.setTotalPages(pageResult.getTotalPages());
        response.setTotalElements(pageResult.getTotalElements());
        response.setHasNext(pageResult.hasNext());
        response.setHasPrevious(pageResult.hasPrevious());
        response.setFirst(pageResult.isFirst());
        response.setLast(pageResult.isLast());
        return  response;
    }






}
