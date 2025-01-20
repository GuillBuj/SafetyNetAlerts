package com.safetynet.safetynet_alert.controller;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.safetynet.safetynet_alert.exception.AlreadyExistsException;
import com.safetynet.safetynet_alert.exception.NotFoundException;
import com.safetynet.safetynet_alert.model.FireStation;
import com.safetynet.safetynet_alert.service.FireStationService;

import dto.ChildAlertResponse;
import dto.FireResponse;
import dto.FireStationResponse;
import dto.FloodDTO;

@RestController
public class FireStationController {
    private final FireStationService fireStationService;

    @Autowired
    public FireStationController(FireStationService fireStationService) {
        this.fireStationService = fireStationService;
    }

    @PostMapping("/firestation")
    @ResponseStatus(HttpStatus.CREATED)
    public void createFireStation(@RequestBody FireStation fireStation)
            throws StreamReadException, DatabindException, IOException {
        fireStationService.createFireStation(fireStation);
    }

    @PutMapping("/firestation")
    @ResponseStatus(HttpStatus.OK)
    public void updateFireStation(@RequestBody FireStation fireStation)
            throws StreamReadException, DatabindException, IOException {
        fireStationService.updateFireStation(fireStation);
    }

    @DeleteMapping("/firestation")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFireStation(@RequestParam(required = false) Integer stationNumber,
            @RequestParam(required = false) String address) throws StreamReadException, DatabindException, IOException {
        if (stationNumber != null) {
            fireStationService.deleteFireStation(stationNumber);
        } else if (address != null) {
            fireStationService.deleteFireStation(address);
        }
    }

    @GetMapping("/firestation")
    public FireStationResponse getPersonsByStation(@RequestParam int stationNumber)
            throws StreamReadException, DatabindException, IOException {

        return fireStationService.getPersonsByStation(stationNumber);
    }

    @GetMapping("/phoneAlert")
    public Set<String> getPhoneNumbersByStation(@RequestParam int stationNumber)
            throws StreamReadException, DatabindException, IOException {

        return fireStationService.getPhoneNumbersByStation(stationNumber);
    }

    @GetMapping("/childAlert")
    public ChildAlertResponse getChildrenByAddress(@RequestParam String address)
            throws StreamReadException, DatabindException, IOException {

        return fireStationService.getChildrenByAdress(address);
    }

    @GetMapping("/fire")
    public FireResponse getPersonsByAddress(@RequestParam String address)
            throws StreamReadException, DatabindException, IOException {

        return fireStationService.getPersonsByAddress(address);
    }

    @GetMapping("/flood")
    public List<FloodDTO> getHomesByStation(@RequestParam List<Integer> stationNumbers)
            throws StreamReadException, DatabindException, IOException {

        return fireStationService.getHomesByStations(stationNumbers);
    }

}
