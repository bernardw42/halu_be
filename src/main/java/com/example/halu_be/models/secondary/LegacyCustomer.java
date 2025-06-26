package com.example.halu_be.models.secondary;

import jakarta.persistence.*;

@Entity
@Table(name = "legacy_customers")
public class LegacyCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username", nullable = false)
    private String name;

    @Column(name = "national_id", nullable = false, unique = true)
    private String nationalId;

    @Column(name = "email")
    private String email;

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getEmail() {
        return email;
    }
}
