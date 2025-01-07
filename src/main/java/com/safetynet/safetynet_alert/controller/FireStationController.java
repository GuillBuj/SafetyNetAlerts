package com.safetynet.safetynet_alert.controller;

import java.io.IOException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.safetynet.safetynet_alert.service.FireStationService;

import dto.FireStationResponse;

@RestController
public class FireStationController {
    private final FireStationService fireStationService;

    @Autowired
    public FireStationController(FireStationService fireStationService){
        this.fireStationService = fireStationService;
    }

    @GetMapping("/firestation")
    public FireStationResponse getPersonsByStation(@RequestParam int stationNumber) throws StreamReadException, DatabindException, IOException{

        return fireStationService.getPersonsByStation(stationNumber);
    }

    @GetMapping("/phoneAlert")
    public Set<String> getPhoneNumbersByStation(@RequestParam int stationNumber) throws StreamReadException, DatabindException, IOException{
        
        return fireStationService.getPhoneNumbersByStation(stationNumber);
    }

   
}
