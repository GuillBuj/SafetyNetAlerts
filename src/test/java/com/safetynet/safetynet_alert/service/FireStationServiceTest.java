package com.safetynet.safetynet_alert.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
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
import com.safetynet.safetynet_alert.model.Datas;
import com.safetynet.safetynet_alert.repository.DataRepository;

import dto.FireStationPersonDTO;
import dto.FireStationResponse;

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


    /* -----------------------------------TEMPLATE
    @Test
    void Test(){
        
    //--GIVEN
        

        //--WHEN
        

        //--THEN
         
    } 
     
     */

}
