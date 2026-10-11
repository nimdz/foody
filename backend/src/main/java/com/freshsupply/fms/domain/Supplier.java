package com.freshsupply.fms.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "suppliers")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String contactEmail;
    private String phone;
    private String address;

    protected Supplier() { } // required by JPA

    public Supplier(String name, String contactEmail, String phone, String address) {
        update(name, contactEmail, phone, address);
    }

    public void update(String name, String contactEmail, String phone, String address) {
        this.name = name;
        this.contactEmail = contactEmail;
        this.phone = phone;
        this.address = address;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getContactEmail() { return contactEmail; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
}
