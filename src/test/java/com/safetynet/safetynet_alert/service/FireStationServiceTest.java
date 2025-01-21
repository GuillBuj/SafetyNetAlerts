package com.safetynet.safetynet_alert.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.safetynet.safetynet_alert.data.DatasTest;
import com.safetynet.safetynet_alert.exception.AlreadyExistsException;
import com.safetynet.safetynet_alert.exception.NotFoundException;
import com.safetynet.safetynet_alert.model.Datas;
import com.safetynet.safetynet_alert.model.FireStation;
import com.safetynet.safetynet_alert.model.Person;
import com.safetynet.safetynet_alert.repository.DataRepository;

import dto.ChildAlertChildDTO;
import dto.ChildAlertResponse;
import dto.FireResponse;
import dto.FireStationPersonDTO;
import dto.FireStationResponse;
import dto.FloodDTO;
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
    void createFireStationTest(){
        FireStation fireStation = new FireStation("3 Rory Rd", 8);

        fireStationService.createFireStation(fireStation);

        verify(dataRepository, times(1)).writeData(datas);
        assertTrue(datas.getFireStations().contains(fireStation));
    }

    @Test
    void createFireStationAlreadyExistsTest(){
        FireStation fireStation = datas.getFireStations().get(0);

        assertThrows(AlreadyExistsException.class, () -> fireStationService.createFireStation(fireStation));

        verify(dataRepository, never()).writeData(datas);  
    }
    
    @Test
    void updateFireStationTest(){
        FireStation fireStation = datas.getFireStations().get(0);
        FireStation updatedFireStation = new FireStation(fireStation.getAddress(), 999);
        
        fireStationService.updateFireStation(updatedFireStation);

        verify(dataRepository, times(1)).writeData(datas);
        assertTrue(datas.getFireStations().contains(updatedFireStation));
        assertFalse(datas.getFireStations().contains(fireStation));
    }

    @Test
    void updateFireStationNotFoundTest(){
        FireStation fireStation = new FireStation("Not found address", 8);
        
        assertThrows(NotFoundException.class,
                    () -> fireStationService.updateFireStation(fireStation));
        
        verify(dataRepository, never()).writeData(datas);  
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

    @Test
    void getHomesByStationsTest(){
        
        //--GIVEN
        List<FloodDTO> expectedResponse = List.of(
            new FloodDTO(3, 
                Map.of(
                    "505 Oak St",
                    Set.of(
                        new PersonWithMedicalDTO("Jackson", "Harris", "123-456-7910", 30, List.of("Aspirin"), List.of("Dust", "Milk")),
                        new PersonWithMedicalDTO("Harper", "Harris", "123-456-7911", 30, List.of("Metformin", "Lisinopril"), List.of("Eggs", "Wheat"))
                    ),
                    "707 Willow St",
                    Set.of(
                        new PersonWithMedicalDTO("Lily", "Green", "123-456-7913", 32, List.of("Insulin", "Aspirin"), List.of("Peanuts", "Soy"))
                    )
                )
            ),
            new FloodDTO(4, 
                Map.of("808 Will St",
                        Set.of(
                            new PersonWithMedicalDTO("James", "Marshall", "123-456-7914", 32, List.of(), List.of())
                            ))))
            ;            
        
        //--WHEN
        List<FloodDTO> actualResponse = fireStationService.getHomesByStations(List.of(3,4));

        //--THEN
        assertEquals(expectedResponse, actualResponse); 
    } 

    /* -----------------------------------TEMPLATE
    @Test
    void Test(){
        
    //--GIVEN
        

        //--WHEN
        

        //--THEN
         
    } 
     
     */

}
