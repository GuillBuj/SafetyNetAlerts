package com.safetynet.safetynet_alert.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.safetynet.safetynet_alert.data.DatasTest;
import com.safetynet.safetynet_alert.model.Datas;
import com.safetynet.safetynet_alert.model.MedicalRecord;
import com.safetynet.safetynet_alert.model.Person;
import com.safetynet.safetynet_alert.repository.DataRepository;

import dto.ChildAlertChildDTO;
import dto.ChildAlertResponse;
import dto.FireResponse;
import dto.FireStationPersonDTO;
import dto.FireStationResponse;
import dto.PersonWithMedicalDTO;

@ExtendWith(MockitoExtension.class)
public class FireStationServiceTest {
    
    Datas datas;
    
    @Mock
    DataRepository dataRepository;

    @InjectMocks
    FireStationService fireStationService;

    @BeforeEach
    void setUp(){
        DatasTest datasTest = new DatasTest();
        datas = datasTest.getDatas();
        when(dataRepository.readData()).thenReturn(datas);
    }

    @Test
    void getPersonsByStationTest() throws StreamReadException, DatabindException, IOException{
        
        //--GIVEN
        FireStationResponse expectedResponse = 
            new FireStationResponse(
                Set.of(
                    new FireStationPersonDTO("Lucas","Johnson", "101 Cherry St", "123-456-7900"),
                    new FireStationPersonDTO("Sophia","Johnson", "101 Cherry St", "123-456-7901"),
                    new FireStationPersonDTO("Ethan","Johnson", "101 Cherry St", "123-456-7902"),    
                    new FireStationPersonDTO("Olivia","Johnson", "101 Cherry St", "123-456-7903"),
                    new FireStationPersonDTO("Evan","Morris", "606 Elm St", "123-456-7912")),
                3,
                2);
        
        //--WHEN
        FireStationResponse actualResponse = fireStationService.getPersonsByStation(1);

        //--THEN
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getChildrenByAdressTest(){
        
        //--GIVEN
        ChildAlertResponse expectedResponse
            = new ChildAlertResponse(
                Set.of(
                    new ChildAlertChildDTO("Olivia", "Johnson", 7),
                    new ChildAlertChildDTO("Ethan", "Johnson", 9)),
                Set.of(
                    new Person("Lucas", "Johnson", "101 Cherry St", "Culver", "97451", "123-456-7900", "lucas.johnson@example.com"),
                    new Person("Sophia", "Johnson", "101 Cherry St", "Culver", "97451", "123-456-7901", "sophia.johnson@example.com")
                    )
                );

        //--WHEN
        ChildAlertResponse actualResponse = fireStationService.getChildrenByAdress("101 Cherry St");

        //--THEN
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getPhoneNumbersByStationTest(){
        
        //--GIVEN
        Set<String> expectedSet = Set.of("123-456-7910", "123-456-7911","123-456-7913");

        //--WHEN
        Set<String> actualSet = fireStationService.getPhoneNumbersByStation(3);

        //--THEN
        assertEquals(expectedSet, actualSet);
    }

    @Test
    void getPersonsByAddressTest(){
        
        //--GIVEN
        FireResponse expectedResponse = new FireResponse(
            Set.of(
                new PersonWithMedicalDTO("Jackson", "Harris", "123-456-7910", 30, List.of("Aspirin"), List.of("Dust","Milk")),
                new PersonWithMedicalDTO("Harper", "Harris", "123-456-7911", 30, List.of("Metformin", "Lisinopril"), List.of("Eggs", "Wheat"))
            ),
            3);

        //--WHEN
        FireResponse actualResponse = fireStationService.getPersonsByAddress("505 Oak St");

        //--THEN
        assertEquals(expectedResponse, actualResponse);
    } 

/*public Set<PersonWithMedicalDTO> getPersonSetByAddress(String address){
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
    
    public FireResponse getPersonsByAddress(String address){
        logger.debug("Getting persons by address({})", address);

        logger.info("List of people living at {}: ", address, getPersonSetByAddress(address));
        return new FireResponse(getPersonSetByAddress(address), getStationByAddress(address));
    }*/

    /* -----------------------------------TEMPLATE
    @Test
    void Test(){
        
    //--GIVEN
        

        //--WHEN
        

        //--THEN
         
    } 
     
     */

}
