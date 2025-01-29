package com.safetynet.safetynet_alert.model;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * The `MedicalRecord` class represents the medical information of a person within the safety net system.
 * It contains details about the person's name, birthdate, medications, and allergies.
 * This class is used to store and manage medical data for individuals in the system.
 */
@Data
@AllArgsConstructor
public class MedicalRecord {

    // The first name of the individual associated with the medical record
    private String firstName;

    // The last name of the individual associated with the medical record
    private String lastName;

    // The birthdate of the individual, formatted as MM/dd/yyyy
    @JsonFormat(pattern = "MM/dd/yyyy") // Jackson annotation for date format
    private LocalDate birthdate;

    // A list of medications prescribed to the individual
    private List<String> medications;

    // A list of allergies the individual has
    private List<String> allergies;
}

