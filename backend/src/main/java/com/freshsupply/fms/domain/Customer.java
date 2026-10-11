package com.freshsupply.fms.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerTier tier;

    protected Customer() { }

    public Customer(String name, String email, String phone, String address, CustomerTier tier) {
        update(name, email, phone, address, tier);
    }

    public void update(String name, String email, String phone, String address, CustomerTier tier) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.tier = tier;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public CustomerTier getTier() { return tier; }
}
