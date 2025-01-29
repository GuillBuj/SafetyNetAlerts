package com.safetynet.safetynet_alert.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * The `Datas` class serves as a container for various types of data related to the safety net system. 
 * It holds lists of `Person`, `FireStation`, and `MedicalRecord` objects that are part of the data 
 * to be serialized and deserialized from a file.
 * <p>
 * This class uses Jackson annotations to map JSON properties to Java fields and is used 
 * for storing the application's core data.
 * </p>
 */
@Data
public class Datas {

    @JsonProperty("persons") // Maps the "persons" field in the JSON to the 'persons' list
    private List<Person> persons;
    
    @JsonProperty("firestations") // Maps the "firestations" field in the JSON to the 'fireStations' list
    private List<FireStation> fireStations;
    
    @JsonProperty("medicalrecords") // Maps the "medicalrecords" field in the JSON to the 'medicalRecords' list
    private List<MedicalRecord> medicalRecords;

    /**
     * Default constructor that initializes the lists for persons, fire stations, and medical records.
     */
    public Datas(){
        this.persons = new ArrayList<>();
        this.fireStations = new ArrayList<>();
        this.medicalRecords = new ArrayList<>();
    }
 
    /**
     * Converts the `Datas` object to a string representation. This method provides a readable 
     * format for displaying the contents of the `Datas` object, including lists of persons, fire stations,
     * and medical records.
     * 
     * @return A string representation of the `Datas` object, including details of persons, 
     *         fire stations, and medical records.
     */
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();

        sb.append("Persons:\n");
        for(Person person : persons){
            sb.append(person).append("\n");
        }

        sb.append("\nFire Stations:\n");
        for (FireStation fireStation : fireStations) {
            sb.append(fireStation).append("\n");
        }

        sb.append("\nMedical Records:\n");
        for (MedicalRecord medicalRecord : medicalRecords) {
            sb.append(medicalRecord).append("\n");
        }

        return sb.toString();
    }
}
