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
import com.safetynet.safetynet_alert.exception.AlreadyExistsException;
import com.safetynet.safetynet_alert.exception.NotFoundException;
import com.safetynet.safetynet_alert.model.Datas;
import com.safetynet.safetynet_alert.model.FireStation;
import com.safetynet.safetynet_alert.model.MedicalRecord;
import com.safetynet.safetynet_alert.model.Person;
import com.safetynet.safetynet_alert.repository.DataRepository;

import dto.ChildAlertChildDTO;
import dto.ChildAlertResponse;
import dto.FireResponse;
import dto.FireStationPersonDTO;
import dto.FireStationResponse;
import dto.FloodDTO;
import dto.PersonWithMedicalDTO;

@Service
public class FireStationService {

    private final Logger logger = LogManager.getLogger(FireStationService.class);
    private final DataRepository dataRepository;

    @Autowired
    public FireStationService(DataRepository dataRepository){
        this.dataRepository = dataRepository;
    }
    
    public void createFireStation(FireStation fireStation) throws StreamReadException, DatabindException, IOException{
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
    
    public void updateFireStation(FireStation fireStation) throws StreamReadException, DatabindException, IOException{
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

    public void deleteFireStation(String address) throws StreamReadException, DatabindException, IOException{
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

    public void deleteFireStation(int stationNumber) throws StreamReadException, DatabindException, IOException{
        logger.debug("Deleting mapping for firestation number {}", stationNumber);
        
        if(getAdressesByStation(stationNumber)!=null){
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

    public Set<String> getPhoneNumbersByStation(int stationNumber) throws StreamReadException, DatabindException, IOException{
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

    public Set<String> getAdressesByStation(int stationNumber) throws StreamReadException, DatabindException, IOException{
        Datas datas = dataRepository.readData();
        List<FireStation> fireStations = datas.getFireStations();

        return fireStations.stream()
                .filter(fireStation -> stationNumber == fireStation.getStation())
                .map(FireStation::getAddress)
                .collect(Collectors.toSet());
    }

    public int getStationByAddress(String address) throws StreamReadException, DatabindException, IOException{
        Datas datas = dataRepository.readData();

        FireStation fireStation = datas.getFireStations().stream()
            .filter(station -> station.getAddress().equals(address))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No fire station found for address: " + address));

        return fireStation.getStation();
    }

    public ChildAlertResponse getChildrenByAdress(String address) throws StreamReadException, DatabindException, IOException{
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
        
        logger.info("List of children living at {}: {}; Other people living there: {}", address, children, otherPersons);
        return new ChildAlertResponse(children, otherPersons);
    }

    public Set<PersonWithMedicalDTO> getPersonSetByAddress(String address) throws StreamReadException, DatabindException, IOException{
        logger.debug("Getting persons(set) by address({})", address);

        Datas datas = dataRepository.readData();
        PersonService personService = new PersonService(dataRepository);

        Set<Person> persons = datas.getPersons().stream()
            .filter(person -> person.getAddress().equals(address))
            .collect(Collectors.toSet());

        Map<Person, MedicalRecord> personsMap = personService.mapPersonToMedicalRecord(persons);

        Set<PersonWithMedicalDTO> personsDTO = personsMap.entrySet().stream()
            .map(entry -> {
                try {
                    return new PersonWithMedicalDTO(
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

        return personsDTO;
    }
    
    public FireResponse getPersonsByAddress(String address) throws StreamReadException, DatabindException, IOException{
        logger.debug("Getting persons by address({})", address);

        logger.info("List of people living at {}: ", address, getPersonSetByAddress(address));
        return new FireResponse(getPersonSetByAddress(address), getStationByAddress(address));
    }

    public List<FloodDTO> getHomesByStations(List<Integer> stationNumbers) throws StreamReadException, DatabindException, IOException{
        logger.debug("Getting homes by stations({})", stationNumbers);

        List<FloodDTO> floods = stationNumbers.stream()
            .map(stationNumber -> {
                Set<String> stationAddresses = new HashSet<String>();
                try {
                    stationAddresses = getAdressesByStation(stationNumber);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
               
                Map<String, Set<PersonWithMedicalDTO>> homes = stationAddresses.stream()
                    .collect(Collectors.toMap(
                        address -> address,
                        address -> {
                            try {
                                return getPersonSetByAddress(address);
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                                                        return null;
                        }
                    ));
            
                return new FloodDTO(stationNumber, homes);
            })
            .collect(Collectors.toList());

            logger.info("Homes covered by station: {}", floods);
            return floods;
    }

    public boolean isMapped(String address) throws StreamReadException, DatabindException, IOException{
        return dataRepository.readData().getFireStations()
                .stream().map(FireStation::getAddress).toList()
                .contains(address);
    }

}
