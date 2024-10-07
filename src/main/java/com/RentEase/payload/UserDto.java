package com.RentEase.payload;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserDto {

    private long id;

    @NotBlank
    @Size(min = 2 , message = "Name must be minimum 2 characters")
    private String name;

    @NotEmpty
    private String username;


    @Email(message = "Email address is not valid !!")
    private String emailId;

    @NotEmpty
    @Size(min = 3, max = 10 , message = "Password must be minimum of 3 characters and maximum 10 characters !! ")
    private String password;

    private String userRole;


    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
