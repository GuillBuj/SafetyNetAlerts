package com.safetynet.safetynet_alert.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.safetynet.safetynet_alert.data.DatasTest;
import com.safetynet.safetynet_alert.dto.PersonByLastNameDTO;
import com.safetynet.safetynet_alert.dto.PersonFullNameDTO;
import com.safetynet.safetynet_alert.exception.AlreadyExistsException;
import com.safetynet.safetynet_alert.exception.NotFoundException;
import com.safetynet.safetynet_alert.model.Datas;
import com.safetynet.safetynet_alert.model.Person;
import com.safetynet.safetynet_alert.repository.DataRepository;

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
    void createPersonTest(){
        Person person = new Person("John", "Doe", "123 Main St", "Springfield", "99999", "555-555-5555", "john.doe@example.com");

        personService.createPerson(person);

        verify(dataRepository, times(1)).writeData(any(Datas.class));
        assertTrue(datas.getPersons().contains(person));
    }
    
    @Test
    void createPersonAlreadyExistsTest(){
        Person person = datas.getPersons().get(0);

        assertThrows(AlreadyExistsException.class, () -> personService.createPerson(person));

        verify(dataRepository, never()).writeData(any(Datas.class));  
    }

    @Test
    void updatePersonTest(){
        Person person = datas.getPersons().get(0);

        Person updatedPerson = new Person(
            person.getFirstName(),
            person.getLastName(),
            person.getAddress(),
            person.getCity(),
            person.getZip(),
            person.getPhone(),
            "newemail@ex.com"
        );

        personService.updatePerson(updatedPerson);

        verify(dataRepository, times(1)).writeData(any(Datas.class));
        assert(datas.getPersons().contains(updatedPerson));
        assertFalse(datas.getPersons().contains(person));  
    }

    @Test
    void updatePersonNotFoundTest(){
        Person person = new Person("Not","Existing",  null, null, null, null, null);

         assertThrows(NotFoundException.class,
                    () -> personService.updatePerson(person));

        verify(dataRepository, never()).writeData(any(Datas.class));
        assertFalse(datas.getPersons().contains(person));  
    }

    @Test
    void deletePersonTest(){
        Person person = datas.getPersons().get(0);

        personService.deletePerson(new PersonFullNameDTO(person.getFirstName(), person.getLastName()));

        verify(dataRepository, times(2)).writeData(any(Datas.class));//person+medicalRecord
        assertFalse(datas.getPersons().contains(person));
        assertFalse(datas.getMedicalRecords().stream()
                    .anyMatch(medicalRecord -> medicalRecord.getFirstName() == person.getFirstName()
                                        && medicalRecord.getLastName() == person.getLastName()));
    }

    @Test
    void deletePersonNotFoundTest(){
        assertThrows(NotFoundException.class,
                    () -> personService.deletePerson(new PersonFullNameDTO("Not", "Existing")));
        
        verify(dataRepository, never()).writeData(any(Datas.class));
    }
    
    @Test
    void getPersonsByLastNameTest(){
        
        //--GIVEN
        String lastName = "Harris";
        //age has to be valid at all times / same birthdate for the 2
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

    @Test
    void getEmailsByCityTest(){
        
        //--GIVEN
        String city ="Redmond";
        Set<String> expectedSet = Set.of("amelia.scott@example.com", "evan.morris@example.com", "lily.green@example.com");

        //--WHEN
        Set<String> actualSet = personService.getEmailsByCity(city);

        //--THEN
        assertEquals(expectedSet, actualSet);
    } 
    
}
