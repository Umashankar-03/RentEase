package com.RentEase.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class LoginDto {

//    @NotEmpty
    private String username;

    @Email(message = "Email address is not valid !!")
    private String emailId;

//    @NotEmpty
    @Size(min = 3, max = 10 , message = "Password must be minimum of 3 characters and maximum 10 characters ")
    private String password;

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
