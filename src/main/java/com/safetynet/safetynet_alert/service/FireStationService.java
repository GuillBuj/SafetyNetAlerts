package com.safetynet.safetynet_alert.service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.safetynet.safetynet_alert.dto.ChildAlertChildDTO;
import com.safetynet.safetynet_alert.dto.ChildAlertResponse;
import com.safetynet.safetynet_alert.dto.FireResponse;
import com.safetynet.safetynet_alert.dto.FireStationPersonDTO;
import com.safetynet.safetynet_alert.dto.FireStationResponse;
import com.safetynet.safetynet_alert.dto.FloodDTO;
import com.safetynet.safetynet_alert.dto.PersonWithMedicalDTO;
import com.safetynet.safetynet_alert.exception.AlreadyExistsException;
import com.safetynet.safetynet_alert.exception.NotFoundException;
import com.safetynet.safetynet_alert.model.Datas;
import com.safetynet.safetynet_alert.model.FireStation;
import com.safetynet.safetynet_alert.model.MedicalRecord;
import com.safetynet.safetynet_alert.model.Person;
import com.safetynet.safetynet_alert.repository.DataRepository;

/**
 * Service class responsible for managing fire station operations including creating, updating,
 * deleting fire stations, retrieving people covered by a fire station, and other related functionalities.
 */
@Service
public class FireStationService {

    private final Logger logger = LogManager.getLogger(FireStationService.class);
    private final DataRepository dataRepository;

    /**
     * Constructs a FireStationService with the given DataRepository.
     *
     * @param dataRepository the DataRepository used to read and write data
     */
    @Autowired
    public FireStationService(DataRepository dataRepository){
        this.dataRepository = dataRepository;
    }
    
    /**
     * Creates a new fire station-address mapping.
     * 
     * @param fireStation the FireStation object containing the mapping details
     * @throws AlreadyExistsException if the mapping for the given address already exists
     */
    public void createFireStation(FireStation fireStation){
        logger.debug("Creating firestation-address mapping({})", fireStation);

        if(!isMapped(fireStation.getAddress())){
            Datas datas = dataRepository.readData();
            List<FireStation> fireStations = datas.getFireStations();    
            fireStations.add(fireStation);
            datas.setFireStations(fireStations);
            dataRepository.writeData(datas);
            logger.info("Firestation-address mapping created({})", fireStation);
        } else{
            logger.error("Mapping for this address already exists({})", fireStation);
            throw new AlreadyExistsException("Mapping for this address already exists: " + fireStation);
        }  
    }
    
    /**
     * Updates an existing fire station-address mapping.
     * 
     * @param fireStation the FireStation object containing the updated mapping details
     * @throws NotFoundException if no mapping for the given address exists
     */
    public void updateFireStation(FireStation fireStation){
        logger.debug("Updating firestation-address mapping({})", fireStation);

        if(isMapped(fireStation.getAddress())){
            Datas datas = dataRepository.readData();
            List<FireStation> fireStations = datas.getFireStations();
            fireStations.removeIf(fireStationTest -> fireStationTest.getAddress().equals(fireStation.getAddress()));
            fireStations.add(fireStation);
            datas.setFireStations(fireStations);
            dataRepository.writeData(datas);
            logger.info("Firestation-address mapping updated({})", fireStation);
        } else{
            logger.error("Mapping for this address not found ({})", fireStation.getAddress());
            throw new NotFoundException("Mapping for this address not found (" + fireStation.getAddress() + ")");
        }   
    }

    /**
     * Deletes the fire station-address mapping for the given address.
     * 
     * @param address the address to remove the mapping for
     * @throws NotFoundException if no mapping for the given address exists
     */
    public void deleteFireStation(String address){
        logger.debug("Deleting mapping for address {}", address);

        if(isMapped(address)){
            Datas datas = dataRepository.readData();
            List<FireStation> fireStations = datas.getFireStations();
            fireStations.removeIf(fireStation -> fireStation.getAddress().equalsIgnoreCase(address));            
            datas.setFireStations(fireStations);
            dataRepository.writeData(datas);
            logger.info("Firestation-address mapping deleted for address({})", address);
        } else{
            logger.error("Mapping for this address not found ({})", address);
            throw new NotFoundException("Mapping for this address not found (" + address + ")");
        } 
    }

    /**
     * Deletes the fire station-address mapping for the given station number.
     * 
     * @param stationNumber the station number to remove the mapping for
     * @throws NotFoundException if no mapping for the given station number exists
     */
    public void deleteFireStation(int stationNumber){
        logger.debug("Deleting mapping for firestation number {}", stationNumber);
        
        if(!getAdressesByStation(stationNumber).isEmpty()){
            Datas datas = dataRepository.readData();
            List<FireStation> fireStations = datas.getFireStations();
            fireStations.removeIf(fireStation -> fireStation.getStation() == stationNumber);
            datas.setFireStations(fireStations);
            dataRepository.writeData(datas);
        } else{
            logger.error("Mapping for this station not found ({})", stationNumber);
            throw new NotFoundException("Mapping for this station not found (" + stationNumber + ")");
        }
    }

    /**
     * Retrieves the list of persons covered by a fire station, based on the station number.
     * 
     * @param stationNumber the station number for which the people should be retrieved
     * @return a FireStationResponse containing the people covered by the station
     * @throws StreamReadException if an error occurs while reading the data
     * @throws DatabindException if an error occurs while binding the data
     * @throws IOException if an error occurs during input/output operations
     */
    public FireStationResponse getPersonsByStation(int stationNumber) throws StreamReadException, DatabindException, IOException{
        logger.debug("Getting list of people covered by station {}", stationNumber);

        Datas datas = dataRepository.readData();
        PersonService personService = new PersonService(dataRepository);

        Set <String> addresses = getAdressesByStation(stationNumber);
        
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

        logger.info("List of people covered by station {}: {}; {} adults; {} children", stationNumber, personsDTO, nbAdults, nbChildren);
        return new FireStationResponse(personsDTO, nbAdults, nbChildren);
    }

    /**
     * Retrieves the phone numbers of all people covered by a fire station.
     * 
     * @param stationNumber the station number for which the phone numbers should be retrieved
     * @return a set of phone numbers of people covered by the station
     */
    public Set<String> getPhoneNumbersByStation(int stationNumber){
        logger.debug("Getting list of phone numbers of people covered by station {}", stationNumber);
        
        Datas datas = dataRepository.readData();

        Set <String> addresses = getAdressesByStation(stationNumber);
        Set<Person> persons = datas.getPersons().stream()
            .filter(person -> addresses.contains(person.getAddress()))
            .collect(Collectors.toSet());
        
        Set<String> numbers = persons.stream()
                                .map(person -> person.getPhone())
                                .collect(Collectors.toSet());

        logger.info("List of phone numbers of people covered by station {}: {}", stationNumber, numbers);
        return numbers;
    }

    /**
     * Retrieves the addresses of all people covered by a fire station, based on the station number.
     * 
     * @param stationNumber the station number for which the addresses should be retrieved
     * @return a set of addresses of people covered by the station
     */
    public Set<String> getAdressesByStation(int stationNumber){
        Datas datas = dataRepository.readData();
        List<FireStation> fireStations = datas.getFireStations();

        return fireStations.stream()
                .filter(fireStation -> stationNumber == fireStation.getStation())
                .map(FireStation::getAddress)
                .collect(Collectors.toSet());
    }

    /**
     * Retrieves the station number for a given address.
     * 
     * @param address the address for which the station number should be retrieved
     * @return the station number that covers the given address
     * @throws IllegalArgumentException if no station exists for the given address
     */
    public int getStationByAddress(String address){
        Datas datas = dataRepository.readData();

        FireStation fireStation = datas.getFireStations().stream()
            .filter(station -> station.getAddress().equals(address))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No fire station found for address: " + address));

        return fireStation.getStation();
    }

    /**
     * Retrieves a list of children living at a given address.
     * 
     * @param address the address to check for children
     * @return a ChildAlertResponse containing children and other people living at the address
     */
    public ChildAlertResponse getChildrenByAdress(String address){
        logger.debug("Getting children by address({})", address);

        Datas datas = dataRepository.readData();
        PersonService personService = new PersonService(dataRepository);

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
                    return new ChildAlertChildDTO(
                            person.getFirstName(),
                            person.getLastName(),
                            personService.getAge(person));
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
        
        logger.info("List of children living at {}: {}; Other people living there: {}", address, children, otherPersons);
        return new ChildAlertResponse(children, otherPersons);
    }

    /**
     * Retrieves the list of persons with medical information by a given address.
     * 
     * @param address the address to check for persons
     * @return a set of PersonWithMedicalDTO containing persons with medical information at the address
     */
    public Set<PersonWithMedicalDTO> getPersonSetByAddress(String address){
        logger.debug("Getting persons(set) by address({})", address);

        Datas datas = dataRepository.readData();
        PersonService personService = new PersonService(dataRepository);

        Set<Person> persons = datas.getPersons().stream()
            .filter(person -> person.getAddress().equals(address))
            .collect(Collectors.toSet());

        Map<Person, MedicalRecord> personsMap = personService.mapPersonToMedicalRecord(persons);

        Set<PersonWithMedicalDTO> personsDTO = personsMap.entrySet().stream()
            .map(entry -> {
                    return new PersonWithMedicalDTO(
                        entry.getKey().getFirstName(),
                        entry.getKey().getLastName(),
                        entry.getKey().getPhone(),
                        personService.getAge(entry.getKey()),
                        entry.getValue().getMedications(),
                        entry.getValue().getAllergies());
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        return personsDTO;
    }
    
    /**
     * Retrieves the list of persons living at a given address, along with the fire station information.
     * 
     * @param address the address to retrieve persons from
     * @return a FireResponse containing persons at the address and their fire station
     */
    public FireResponse getPersonsByAddress(String address){
        logger.debug("Getting persons by address({})", address);

        logger.info("List of people living at {}: ", address, getPersonSetByAddress(address));
        return new FireResponse(getPersonSetByAddress(address), getStationByAddress(address));
    }

    /**
     * Retrieves the list of homes covered by multiple stations.
     * 
     * @param stationNumbers a list of station numbers to retrieve homes for
     * @return a list of FloodDTO containing homes covered by each station
     */
    public List<FloodDTO> getHomesByStations(List<Integer> stationNumbers){
        logger.debug("Getting homes by stations({})", stationNumbers);

        List<FloodDTO> floods = stationNumbers.stream()
            .map(stationNumber -> {
                Set<String> stationAddresses = new HashSet<String>();
                stationAddresses = getAdressesByStation(stationNumber);
               
                Map<String, Set<PersonWithMedicalDTO>> homes = stationAddresses.stream()
                    .collect(Collectors.toMap(
                        address -> address,
                        address -> {
                            return getPersonSetByAddress(address); }
                    ));
            
                return new FloodDTO(stationNumber, homes);
            })
            .collect(Collectors.toList());

            logger.info("Homes covered by station: {}", floods);
            return floods;
    }

    /**
     * Checks if an address is already mapped to a fire station.
     * 
     * @param address the address to check
     * @return true if the address is already mapped, false otherwise
     */
    public boolean isMapped(String address){
        return dataRepository.readData().getFireStations()
                    .stream()
                    .map(FireStation::getAddress).toList()
                    .contains(address);
    }

}
