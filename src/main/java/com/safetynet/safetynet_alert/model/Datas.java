package com.safetynet.safetynet_alert.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Datas {
    
    private List<Person> persons;
    
    @JsonProperty("firestations")
    private List<FireStation> fireStations;
    
    @JsonProperty("medicalrecords")
    private List<MedicalRecord> medicalRecords;

    public Datas(){
        this.persons = new ArrayList<>();
        this.fireStations = new ArrayList<>();
        this.medicalRecords = new ArrayList<>();
    }

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
