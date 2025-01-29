package com.safetynet.safetynet_alert.controller;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.safetynet.safetynet_alert.dto.ChildAlertResponse;
import com.safetynet.safetynet_alert.dto.FireResponse;
import com.safetynet.safetynet_alert.dto.FireStationResponse;
import com.safetynet.safetynet_alert.dto.FloodDTO;
import com.safetynet.safetynet_alert.model.FireStation;
import com.safetynet.safetynet_alert.service.FireStationService;

/**
 * Controller for managing fire station-related endpoints.
 */
@RestController
public class FireStationController {
    private final FireStationService fireStationService;

    /**
     * Constructor to initialize FireStationController with FireStationService.
     * 
     * @param fireStationService The service handling fire station operations.
     */
    @Autowired
    public FireStationController(FireStationService fireStationService) {
        this.fireStationService = fireStationService;
    }

    /**
     * Creates a new fire station.
     * 
     * @param fireStation The fire station to be created.
     */
    @PostMapping("/firestation")
    @ResponseStatus(HttpStatus.CREATED)
    public void createFireStation(@RequestBody FireStation fireStation) {
        fireStationService.createFireStation(fireStation);
    }

    /**
     * Updates an existing fire station.
     * 
     * @param fireStation The fire station with updated information.
     */
    @PutMapping("/firestation")
    @ResponseStatus(HttpStatus.OK)
    public void updateFireStation(@RequestBody FireStation fireStation) {
        fireStationService.updateFireStation(fireStation);
    }

    /**
     * Deletes a fire station based on station number or address.
     * 
     * @param stationNumber The station number to delete (optional).
     * @param address The address of the fire station to delete (optional).
     */
    @DeleteMapping("/firestation")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFireStation(@RequestParam(required = false) Integer stationNumber,
            @RequestParam(required = false) String address){
        if (stationNumber != null) {
            fireStationService.deleteFireStation(stationNumber);
        } else if (address != null) {
            fireStationService.deleteFireStation(address);
        }
    }

    /**
     * Retrieves persons covered by a specific fire station.
     * 
     * @param stationNumber The fire station number.
     * @return A FireStationResponse object containing relevant data.
     * @throws StreamReadException If an error occurs while reading the stream.
     * @throws DatabindException If an error occurs while binding data.
     * @throws IOException If an I/O exception occurs.
     */
    @GetMapping("/firestation")
    public FireStationResponse getPersonsByStation(@RequestParam int stationNumber)
            throws StreamReadException, DatabindException, IOException {

        return fireStationService.getPersonsByStation(stationNumber);
    }

    /**
     * Retrieves phone numbers of residents covered by a fire station.
     * 
     * @param stationNumber The fire station number.
     * @return A set of phone numbers.
     */
    @GetMapping("/phoneAlert")
    public Set<String> getPhoneNumbersByStation(@RequestParam int stationNumber){

        return fireStationService.getPhoneNumbersByStation(stationNumber);
    }

    /**
     * Retrieves children living at a specific address.
     * 
     * @param address The address to search for children.
     * @return A ChildAlertResponse containing children and family details.
     */
    @GetMapping("/childAlert")
    public ChildAlertResponse getChildrenByAddress(@RequestParam String address){

        return fireStationService.getChildrenByAdress(address);
    }

    /**
     * Retrieves persons living at a specific address in case of a fire.
     * 
     * @param address The address to search.
     * @return A FireResponse containing residents' information.
     */
    @GetMapping("/fire")
    public FireResponse getPersonsByAddress(@RequestParam String address){

        return fireStationService.getPersonsByAddress(address);
    }

    /**
     * Retrieves homes covered by multiple fire stations.
     * 
     * @param stationNumbers A list of fire station numbers.
     * @return A list of FloodDTO containing household details.
     */
    @GetMapping("/flood")
    public List<FloodDTO> getHomesByStation(@RequestParam List<Integer> stationNumbers){

        return fireStationService.getHomesByStations(stationNumbers);
    }
}
