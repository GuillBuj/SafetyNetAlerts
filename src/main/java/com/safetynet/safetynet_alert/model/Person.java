package com.safetynet.safetynet_alert.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * The `Person` class represents an individual person within the safety net system.
 * It holds basic personal information, including the person's name, address, city, zip code,
 * phone number, and email address.
 * This class is used to store and manage information about people in the system.
 */
@Data
@AllArgsConstructor
public class Person {

    // The first name of the individual
    private String firstName;

    // The last name of the individual
    private String lastName;

    // The address where the individual resides
    private String address;

    // The city where the individual resides
    private String city;

    // The zip code of the individual's residence
    private String zip;

    // The phone number of the individual
    private String phone;

    // The email address of the individual
    private String email;
}

