package com.safetynet.safetynet_alert.model;

import java.util.List;

import lombok.Data;

@Data
public class MedicalRecord {
    
    private String firstName;
    private String lastName;
    // @JsonFormat(pattern = "MM/dd/yyyy")
    // private Date birthdate;
    private String birthdate;
    private List<String> medications;
    private List<String> allergies;
}
