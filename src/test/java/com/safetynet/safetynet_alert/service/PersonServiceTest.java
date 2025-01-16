package com.safetynet.safetynet_alert.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.safetynet.safetynet_alert.data.DatasTest;
import com.safetynet.safetynet_alert.model.Datas;
import com.safetynet.safetynet_alert.model.Person;
import com.safetynet.safetynet_alert.repository.DataRepository;

import dto.PersonByLastNameDTO;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {
    
    private Datas datas;

    @Mock
    private DataRepository dataRepository;

    @InjectMocks
    private PersonService personService;

    @BeforeEach
    void setUp(){
        DatasTest datasTest = new DatasTest();
        datas = datasTest.getDatas();
        when(dataRepository.readData()).thenReturn(datas);
    }

    @Test
    void getPersonsByLastNameTest(){
        //--GIVEN
        String lastName = "Harris";
        //age has to be valid at all times
        int age = Period.between(LocalDate.of(1994, 10, 30), LocalDate.now()).getYears();
        List<PersonByLastNameDTO> expectedList = List.of(
            new PersonByLastNameDTO
                ("Harper",
                "Harris", 
                "123-456-7911", 
                age,
                "harris.allen@example.com", 
                List.of("Metformin", "Lisinopril"), 
                List.of("Eggs", "Wheat")),
            new PersonByLastNameDTO
                ("Jackson",
                "Harris",
                "123-456-7910",
                age,
                "jackson.harris@example.com",
                List.of("Aspirin"),
                List.of("Dust", "Milk"))
        );

        //--WHEN
        List<PersonByLastNameDTO> actualList = personService.getPersonsByLastName(lastName);

        //--THEN
        assertEquals(expectedList, actualList); 
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
