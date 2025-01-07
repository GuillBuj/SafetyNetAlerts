package com.safetynet.safetynet_alert.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import com.safetynet.safetynet_alert.model.FireStation;
import com.safetynet.safetynet_alert.model.MedicalRecord;
import com.safetynet.safetynet_alert.model.Person;

import dto.ChildAlertChildDTO;
import dto.ChildAlertResponse;
import dto.FirePersonDTO;
import dto.FireResponse;
import dto.FireStationPersonDTO;
import dto.FireStationResponse;

@Service
public class FireStationService {

    private final Logger logger = LogManager.getLogger(FireStationService.class);
    private final DataService dataService;

    @Autowired
    public FireStationService(DataService dataService){
        this.dataService = dataService;
    }
    
    public FireStationResponse getPersonsByStation(int stationNumber) throws StreamReadException, DatabindException, IOException{

        Datas datas = dataService.readData();
        PersonService personService = new PersonService(dataService);

        Set <String> addresses = getAdressesByStation(datas.getFireStations(), stationNumber);
        
        Set<Person> persons = datas.getPersons().stream()
                .filter(person -> addresses.contains(person.getAddress()))
                .collect(Collectors.toSet());
        Set<FireStationPersonDTO> personsDTO = persons.stream()
                .map(person -> new FireStationPersonDTO(
                    person.getFirstName(),
                    person.getLastName(),
                    person.getAddress(),
                    person.getPhone()))
                .collect(Collectors.toSet());       
        
        int nbAdults=0, nbChildren=0;
        for(Person person:persons){
            if(personService.isAdult(person)){
                nbAdults++;
            } else if(personService.isChild(person)){
                nbChildren++;
            }
        }

        return new FireStationResponse(personsDTO, nbAdults, nbChildren);
    }

    public Set<String> getPhoneNumbersByStation(int fireStationNumber) throws StreamReadException, DatabindException, IOException{
        Datas datas = dataService.readData();
        PersonService personService = new PersonService(dataService);

        Set <String> addresses = getAdressesByStation(datas.getFireStations(), fireStationNumber);
        Set<Person> persons = datas.getPersons().stream()
            .filter(person -> addresses.contains(person.getAddress()))
            .collect(Collectors.toSet());
        
        return persons.stream()
            .map(person -> person.getPhone())
            .collect(Collectors.toSet());
    }

    public Set<String> getAdressesByStation(List<FireStation> fireStations, int stationNumber){
        
        return fireStations.stream()
                .filter(fireStation -> stationNumber == fireStation.getStation())
                .map(FireStation::getAddress)
                .collect(Collectors.toSet());
    }

    public int getStationByAddress(String address) throws StreamReadException, DatabindException, IOException{
        Datas datas = dataService.readData();

        FireStation fireStation = datas.getFireStations().stream()
            .filter(station -> station.getAddress().equals(address))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No fire station found for address: " + address));

        return fireStation.getStation();
    }

    public ChildAlertResponse getChildrenByAdress(String address) throws StreamReadException, DatabindException, IOException{
        logger.info("Getting children by address({})", address);

        Datas datas = dataService.readData();
        PersonService personService = new PersonService(dataService);

        Set<Person> persons = datas.getPersons().stream()
            .filter(person -> person.getAddress().equals(address))
            .collect(Collectors.toSet());

        Set<ChildAlertChildDTO> children = persons.stream()
            .filter(person -> {
                try {
                    return personService.isChild(person);
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
                            personService.getAge(person));
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
                    return !personService.isChild(person);
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
        PersonService personService = new PersonService(dataService);

        Set<Person> persons = datas.getPersons().stream()
            .filter(person -> person.getAddress().equals(address))
            .collect(Collectors.toSet());

        Map<Person, MedicalRecord> personsMap = personService.mapPersonToMedicalRecord(persons);

        Set<FirePersonDTO> personsDTO = personsMap.entrySet().stream()
            .map(entry -> {
                try {
                    return new FirePersonDTO(
                        entry.getKey().getFirstName(),
                        entry.getKey().getLastName(),
                        entry.getKey().getPhone(),
                        personService.getAge(entry.getKey()),
                        entry.getValue().getMedications(),
                        entry.getValue().getAllergies());
                } catch (IOException e) {
                    logger.error("Error processing {} {}", entry.getKey().getFirstName(), entry.getKey().getLastName());
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        return new FireResponse(personsDTO, getStationByAddress(address));
    }

}
