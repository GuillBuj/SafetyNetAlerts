package com.safetynet.safetynet_alert.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.safetynet.safetynet_alert.model.Datas;
import com.safetynet.safetynet_alert.model.MedicalRecord;
import com.safetynet.safetynet_alert.model.Person;

@Service
public class PersonService {
    private final Logger logger = LogManager.getLogger(FireStationService.class);
    private final DataService dataService;

    @Autowired
    public PersonService(DataService dataService){
        this.dataService = dataService;
    }

    public Map<Person,MedicalRecord> mapPersonToMedicalRecord(Set<Person> persons) throws StreamReadException, DatabindException, IOException{
        Datas datas = dataService.readData();
        List<MedicalRecord> medicalRecords = datas.getMedicalRecords();
        Map<Person,MedicalRecord> mapPersonMedicalReport = new HashMap<>();

        for(Person person : persons){
            Optional<MedicalRecord> medicalRecord
                = medicalRecords.stream()
                    .filter(record -> record.getFirstName().equals(person.getFirstName())
                        && record.getLastName().equals(record.getLastName()))
                    .findFirst();

            if(medicalRecord.isPresent()){
                mapPersonMedicalReport.put(person, medicalRecord.get());
            } else{
                logger.warn("No medical record(" + person.getFirstName() + " " + person.getLastName() + ")");
            }
        }

        return mapPersonMedicalReport;
    }
}
