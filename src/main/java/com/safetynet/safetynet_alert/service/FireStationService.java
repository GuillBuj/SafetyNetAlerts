package com.safetynet.safetynet_alert.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import com.safetynet.safetynet_alert.model.Person;

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

}
