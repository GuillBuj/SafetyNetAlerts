package com.safetynet.safetynet_alert.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safetynet.safetynet_alert.model.Datas;
import com.safetynet.safetynet_alert.model.FireStation;
import com.safetynet.safetynet_alert.model.Person;

@Service
public class FireStationService {

    private final Logger logger = LogManager.getLogger(FireStationService.class);
    private final DataService dataService;

    @Autowired
    public FireStationService(DataService dataService){
        this.dataService = dataService;
    }

    // public Set<Person> getPersonsByStation(int stationNumber){

    //     Datas datas = dataService.readData();
    //     Set <String> addresses = getAdressesByStation(datas.getFireStations(), stationNumber);

    //     return datas.getPersons().stream()
    //             .filter(person -> addresses.stream())

    // }
    
    public Set<String> getAdressesByStation(List<FireStation> fireStations, int stationNumber){
        
        return fireStations.stream()
                .filter(fireStation -> stationNumber == fireStation.getStation())
                .map(FireStation::getAddress)
                .collect(Collectors.toSet());
    }
}
