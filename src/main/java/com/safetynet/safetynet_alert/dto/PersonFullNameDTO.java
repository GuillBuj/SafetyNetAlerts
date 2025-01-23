package com.safetynet.safetynet_alert.dto;

import java.util.List;
import java.util.Optional;

import com.safetynet.safetynet_alert.model.MedicalRecord;
import com.safetynet.safetynet_alert.model.Person;

public record PersonFullNameDTO(
    String firstName,
    String lastName
) {
    public boolean exists(List<Person> persons){
        return persons.stream()
                    .filter(person -> person.getFirstName().equalsIgnoreCase(firstName)
                            && person.getLastName().equalsIgnoreCase(lastName))
                    .findFirst()
                    .isPresent();
    }

    public Optional<Person> findPerson(List<Person> persons){
        return persons.stream()
                    .filter(person -> person.getFirstName().equalsIgnoreCase(firstName)
                            && person.getLastName().equalsIgnoreCase(lastName))
                    .findFirst();  
    }

    public boolean existsInMeds(List<MedicalRecord> medicalRecords){
        return medicalRecords.stream()
                    .filter(person -> person.getFirstName().equalsIgnoreCase(firstName)
                            && person.getLastName().equalsIgnoreCase(lastName))
                    .findFirst()
                    .isPresent();
    }

    public Optional<MedicalRecord> findMedicalRecord(List<MedicalRecord> medicalRecords){
        return medicalRecords.stream()
                    .filter(medicalRecord -> medicalRecord.getFirstName().equalsIgnoreCase(firstName)
                            && medicalRecord.getLastName().equalsIgnoreCase(lastName))
                    .findFirst();   
    }
}
