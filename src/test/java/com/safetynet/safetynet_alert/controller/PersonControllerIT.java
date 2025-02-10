package com.safetynet.safetynet_alert.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.safetynet_alert.data.DatasTest;
import com.safetynet.safetynet_alert.dto.PersonByLastNameDTO;
import com.safetynet.safetynet_alert.dto.PersonFullNameDTO;
import com.safetynet.safetynet_alert.model.Person;
import com.safetynet.safetynet_alert.repository.DataRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class PersonControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DataRepository dataRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        dataRepository.writeData(new DatasTest().getDatasOneOfEach());
    }

    @Test
    public void createPersonTestCreated() throws JsonProcessingException, Exception{
        Person person = new Person(
            "John", "Doe", "123 Main St", "Springfield", "99999", "555-555-5555", "john.doe@example.com");
    
       
        mockMvc.perform(post("/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(person)))
                .andExpect(status().isCreated());

        assertTrue(dataRepository.readData().getPersons().contains(person));     
    }

    @Test
    public void createPersonTestAlreadyExists() throws JsonProcessingException, Exception{
        Person person = new Person(
            "Jane", "Doe", "123 Main St", "Springfield", "99999", "333-333-4444", "jane.doe@example.com");
        
        mockMvc.perform(post("/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(person)))
                .andExpect(status().isBadRequest());

        assertFalse(dataRepository.readData().getPersons().contains(person));
    }

    @Test
    public void updatePersonTestOk() throws JsonProcessingException, Exception{
        Person person = new Person(
            "Jane", "Doe", "123 Main St", "Springfield", "99999", "555-555-8888", "john.doe@example.com");
    
        mockMvc.perform(put("/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(person)))
                .andExpect(status().isOk());

        assertTrue(dataRepository.readData().getPersons().contains(person));
    }

    @Test
    public void updatePersonTestNotFound() throws JsonProcessingException, Exception{
        Person person = new Person(
            "John", "Doe", "123 Main St", "Springfield", "99999", "555-555-5555", "john.doe@example.com");
        
        mockMvc.perform(put("/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(person)))
                .andExpect(status().isNotFound());

        assertFalse(dataRepository.readData().getPersons().contains(person));   
    }

    @Test
    public void deletePersonTestOk() throws JsonProcessingException, Exception{
        PersonFullNameDTO personDTO = new PersonFullNameDTO("Jane", "Doe");

        mockMvc.perform(delete("/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(personDTO)))
                .andExpect(status().isOk());

        assertFalse(dataRepository.readData().getPersons().stream()
                    .anyMatch(person -> person.getFirstName().equals(personDTO.firstName())
                                    && person.getLastName().equals(personDTO.lastName())));
        assertFalse(dataRepository.readData().getMedicalRecords().stream()
                    .anyMatch(medicalRecord -> medicalRecord.getFirstName().equals(personDTO.firstName())
                        && medicalRecord.getLastName().equals(personDTO.lastName())));
    }

    @Test
    public void deletePersonTestNotFound() throws JsonProcessingException, Exception{
        PersonFullNameDTO person = new PersonFullNameDTO("John", "Doe");
        
        mockMvc.perform(delete("/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(person)))
                .andExpect(status().isNotFound());   
    }

    @Test
    void personInfoLastNameTest() throws Exception{
        dataRepository.writeData(new DatasTest().getDatasLarge());
        
        //age has to be valid at all times / same birthdate for the 2
        int age = Period.between(LocalDate.of(1994, 10, 30), LocalDate.now()).getYears();
        List<PersonByLastNameDTO> expectedResponse = List.of(
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

        mockMvc.perform(get("/personInfoLastName")
                        .param("lastName","Harris"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    void communityEmailTest() throws JsonProcessingException, Exception{
        dataRepository.writeData(new DatasTest().getDatasLarge());

        Set<String> emails = Set.of("amelia.scott@example.com", "evan.morris@example.com", "lily.green@example.com");

        mockMvc.perform(get("/communityEmail")
                        .param("city","Redmond"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(emails)));          
    }
   
}
