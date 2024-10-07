package com.RentEase.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@Table(name = "app_user")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "username", nullable = false, unique = true, length = 155)
    private String username;

    @Column(name = "email_id", nullable = false, unique = true)
    private String emailId;

    @JsonIgnore
    @Column(name = "password", nullable = false, length = 350)
    private String password;

    @JsonIgnore
    @Column(name = "user_role", nullable = false, length = 150)
    private String userRole;

}