package com.safetynet.safetynet_alert.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.safetynet.safetynet_alert.model.Datas;
import com.safetynet.safetynet_alert.model.MedicalRecord;
import com.safetynet.safetynet_alert.model.Person;

import dto.ChildAlertChildDTO;
import dto.ChildAlertResponse;
import dto.FirePersonDTO;
import dto.FireResponse;

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

    public Optional<MedicalRecord> getMedicalRecord(Person person) throws StreamReadException, DatabindException, IOException{
        Datas datas = dataService.readData();
        List<MedicalRecord> medicalRecords = datas.getMedicalRecords();

        return medicalRecords.stream()
                .filter(record -> record.getFirstName().equals(person.getFirstName())
                    && record.getLastName().equals(person.getLastName()))
                .findFirst();
    }

    public int getAge(MedicalRecord medicalRecord){
        LocalDate birthDate = medicalRecord.getBirthdate();
        LocalDate currentDate = LocalDate.now();

        return Period.between(birthDate, currentDate).getYears();
    }

    public int getAge(Person person) throws StreamReadException, DatabindException, IOException{
        Optional<MedicalRecord> medicalRecord = getMedicalRecord(person);
        if(medicalRecord.isPresent()){
            return getAge(medicalRecord.get());
        } else {
            return -1;
        }
    }

    public boolean isAdult(Person person) throws StreamReadException, DatabindException, IOException{
        return getAge(person)>=18;
    }

    public boolean isChild(Person person) throws StreamReadException, DatabindException, IOException{
        return getAge(person)<18 && getAge(person)!=-1;
    }

    public ChildAlertResponse getChildrenByAdress(String address) throws StreamReadException, DatabindException, IOException{
        logger.info("Getting children by address({})", address);

        Datas datas = dataService.readData();

        Set<Person> persons = datas.getPersons().stream()
            .filter(person -> person.getAddress().equals(address))
            .collect(Collectors.toSet());

        Set<ChildAlertChildDTO> children = persons.stream()
            .filter(person -> {
                try {
                    return isChild(person);
                } catch (IOException e) {
                    logger.error("Error checking if person is a child");
                    return false;
                }
            })
            .map(person -> {
                try {
                    return new ChildAlertChildDTO(
                            person.getFirstName(),
                            person.getLastName(),
                            getAge(person));
                } catch (IOException e) {
                    logger.error("Error calculating age");
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        Set<Person> otherPersons = persons.stream()
            .filter(person -> {
                try {
                    return !isChild(person);
                } catch (IOException e) {
                    logger.error("Error checking if person is a child");
                    return false;
                }
            })
            .collect(Collectors.toSet());
        
        return new ChildAlertResponse(children, otherPersons);
    }

    public FireResponse getPersonsByAddress(String address) throws StreamReadException, DatabindException, IOException{
        logger.info("Getting persons by address({})", address);

        Datas datas = dataService.readData();
        FireStationService fireStationService = new FireStationService(dataService);

        Set<Person> persons = datas.getPersons().stream()
            .filter(person -> person.getAddress().equals(address))
            .collect(Collectors.toSet());

        Map<Person, MedicalRecord> personsMap = mapPersonToMedicalRecord(persons);

        Set<FirePersonDTO> personsDTO = personsMap.entrySet().stream()
            .map(entry -> {
                try {
                    return new FirePersonDTO(
                        entry.getKey().getFirstName(),
                        entry.getKey().getLastName(),
                        entry.getKey().getPhone(),
                        getAge(entry.getKey()),
                        entry.getValue().getMedications(),
                        entry.getValue().getAllergies());
                } catch (IOException e) {
                    logger.error("Error processing {} {}", entry.getKey().getFirstName(), entry.getKey().getLastName());
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        return new FireResponse(personsDTO, fireStationService.getStationByAddress(address));
    }
}
