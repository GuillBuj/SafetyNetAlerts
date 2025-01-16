package com.safetynet.safetynet_alert.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.safetynet.safetynet_alert.model.Datas;
import com.safetynet.safetynet_alert.model.FireStation;
import com.safetynet.safetynet_alert.model.MedicalRecord;
import com.safetynet.safetynet_alert.model.Person;

public class DatasTest {
    
    private List<Person> persons;
    private List<FireStation> fireStations;
    private List<MedicalRecord> medicalRecords;

    public DatasTest(){
            this.persons = new ArrayList<>(List.of(
                    new Person("Lucas", "Johnson", "101 Cherry St", "Culver", "97451", "123-456-7900", "lucas.johnson@example.com"),
                    new Person("Sophia", "Johnson", "101 Cherry St", "Culver", "97451", "123-456-7901", "sophia.johnson@example.com"),
                    new Person("Ethan", "Johnson", "101 Cherry St", "Culver", "97451", "123-456-7902", "ethan.johnson@example.com"),
                    new Person("Olivia", "Johnson", "101 Cherry St", "Culver", "97451", "123-456-7903", "olivia.johnson@example.com"),
                    new Person("Aiden", "Taylor", "202 Pine St", "Culver", "97451", "123-456-7904", "aiden.taylor@example.com"),
                    new Person("Mia", "White", "202 Pine St", "Culver", "97451", "123-456-7905", "mia.white@example.com"),
                    new Person("Isabella", "Lewis", "303 Cedar St", "Culver", "97451", "123-456-7907", "isabella.lewis@example.com"),
                    new Person("Amelia", "Scott", "404 Birch St", "Redmond", "97756", "123-456-7909", "amelia.scott@example.com"),
                    new Person("Jackson", "Harris", "505 Oak St", "Culver", "97451", "123-456-7910", "jackson.harris@example.com"),
                    new Person("Harper", "Harris", "505 Oak St", "Culver", "97451", "123-456-7911", "harris.allen@example.com"),
                    new Person("Evan", "Morris", "606 Elm St", "Redmond", "97756", "123-456-7912", "evan.morris@example.com"),
                    new Person("Lily", "Green", "707 Willow St", "Redmond", "97756", "123-456-7913", "lily.green@example.com")
            ));
        
            this.fireStations = new ArrayList<>(List.of(
                    new FireStation("101 Cherry St", 1),
                    new FireStation("202 Pine St", 2),
                    new FireStation("303 Cedar St", 2),
                    new FireStation("404 Birch St", 2),
                    new FireStation("505 Oak St", 3),
                    new FireStation("606 Elm St", 1),
                    new FireStation("707 Willow St", 3)
            ));
        
            this.medicalRecords = new ArrayList<>(List.of(
                new MedicalRecord("Lucas", "Johnson", LocalDate.of(1992, 3, 15), List.of("Amoxicillin", "Ibuprofen"), List.of("Pollen", "Penicillin")),
                new MedicalRecord("Sophia", "Johnson", LocalDate.of(1995, 7, 10), List.of("Loratadine", "Hydrocodone"), List.of("Peanuts", "Shellfish")),
                new MedicalRecord("Ethan", "Johnson", LocalDate.of(2015, 5, 22), List.of("Aspirin"), List.of("Dust")),
                new MedicalRecord("Olivia", "Johnson", LocalDate.of(2017, 9, 5), List.of("Metformin"), List.of("Latex")),
                new MedicalRecord("Aiden", "Taylor", LocalDate.of(2000, 5, 25), List.of("Lisinopril"), List.of("Milk", "Eggs")),
                new MedicalRecord("Mia", "White", LocalDate.of(1997, 8, 18), List.of("Simvastatin", "Albuterol"), List.of("Wheat", "Mold")),
                new MedicalRecord("Isabella", "Lewis", LocalDate.of(1993, 6, 22), List.of("Prednisone"), List.of("Pollen")),
                new MedicalRecord("Amelia", "Scott", LocalDate.of(1996, 1, 5), List.of("Hydrocodone"), List.of("Shellfish", "Latex")),
                new MedicalRecord("Jackson", "Harris", LocalDate.of(1994, 10, 30), List.of("Aspirin"), List.of("Dust", "Milk")),
                new MedicalRecord("Harper", "Harris", LocalDate.of(1994, 10, 30), List.of("Metformin", "Lisinopril"), List.of("Eggs", "Wheat")),
                new MedicalRecord("Evan", "Morris", LocalDate.of(1980, 4, 20), List.of("Tylenol"), List.of("Penicillin")),
                new MedicalRecord("Lily", "Green", LocalDate.of(1992, 2, 14), List.of("Insulin", "Aspirin"), List.of("Peanuts", "Soy"))
        ));  
    }

    public Datas getDatas(){
        Datas datas = new Datas();
        datas.setPersons(persons);
        datas.setFireStations(fireStations);
        datas.setMedicalRecords(medicalRecords);

        return datas;
    }
}
