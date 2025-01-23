package com.safetynet.safetynet_alert.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.safetynet_alert.dto.PersonByLastNameDTO;
import com.safetynet.safetynet_alert.dto.PersonFullNameDTO;
import com.safetynet.safetynet_alert.exception.AlreadyExistsException;
import com.safetynet.safetynet_alert.exception.NotFoundException;
import com.safetynet.safetynet_alert.model.Person;
import com.safetynet.safetynet_alert.service.PersonService;

@SpringBootTest
@AutoConfigureMockMvc
public class PersonControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonService personService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        objectMapper = new ObjectMapper();
    }

    @Test
    public void createPersonTestCreated() throws JsonProcessingException, Exception{
        Person person = new Person(
            "John", "Doe", "123 Main St", "Springfield", "99999", "555-555-5555", "john.doe@example.com");
    
        doNothing().when(personService).createPerson(any(Person.class));

        mockMvc.perform(post("/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(person)))
                .andExpect(status().isCreated());

        verify(personService, times(1)).createPerson(person);
    }

    @Test
    public void createPersonTestAlreadyExists() throws JsonProcessingException, Exception{
        Person person = new Person(
            "John", "Doe", "123 Main St", "Springfield", "99999", "555-555-5555", "john.doe@example.com");
        
        doThrow(new AlreadyExistsException("Person already exists"))
            .when(personService).createPerson(any(Person.class));

        mockMvc.perform(post("/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(person)))
                .andExpect(status().isBadRequest());

        verify(personService, times(1)).createPerson(person);         
    }

    @Test
    public void updatePersonTestOk() throws JsonProcessingException, Exception{
        Person person = new Person(
            "John", "Doe", "123 Main St", "Springfield", "99999", "555-555-5555", "john.doe@example.com");
    
        doNothing().when(personService).updatePerson(any(Person.class));

        mockMvc.perform(put("/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(person)))
                .andExpect(status().isOk());

        verify(personService, times(1)).updatePerson(person);
    }

    @Test
    public void updatePersonTestNotFound() throws JsonProcessingException, Exception{
        Person person = new Person(
            "John", "Doe", "123 Main St", "Springfield", "99999", "555-555-5555", "john.doe@example.com");
        
        doThrow(new NotFoundException("Person not found"))
            .when(personService).updatePerson(any(Person.class));

        mockMvc.perform(put("/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(person)))
                .andExpect(status().isNotFound());

        verify(personService, times(1)).updatePerson(person);    
    }

    @Test
    public void deletePersonTestOk() throws JsonProcessingException, Exception{
        PersonFullNameDTO person = new PersonFullNameDTO("John", "Doe");

        doNothing().when(personService).deletePerson(any(PersonFullNameDTO.class));

        mockMvc.perform(delete("/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(person)))
                .andExpect(status().isOk());

        verify(personService, times(1)).deletePerson(person);
    }

    @Test
    public void deletePersonTestNotFound() throws JsonProcessingException, Exception{
        PersonFullNameDTO person = new PersonFullNameDTO("John", "Doe");
        
        doThrow(new NotFoundException("Person not found"))
            .when(personService).deletePerson(any(PersonFullNameDTO.class));
        mockMvc.perform(delete("/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(person)))
                .andExpect(status().isNotFound());

        verify(personService, times(1)).deletePerson(person);    
    }

    @Test
    void personInfoLastNameTest() throws Exception{
        String lastName = "Doe";

        List<PersonByLastNameDTO> persons = List.of(
        new PersonByLastNameDTO(
            "John", "Doe", "123-456-7890", 25, "johndoe@example.com",
            List.of("Medication1", "Medication2"), List.of("Allergy1")),
        new PersonByLastNameDTO(
            "Jane", "Doe", "987-654-3210", 30, "janedoe@example.com",
            List.of("Medication3"), List.of("Allergy2", "Allergy3"))
        );

        when(personService.getPersonsByLastName(lastName)).thenReturn(persons);

        mockMvc.perform(get("/personInfoLastName")
                        .param("lastName",lastName))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(persons)));

        verify(personService, times(1)).getPersonsByLastName(lastName);
    }

    @Test
    void communityEmailTest() throws JsonProcessingException, Exception{
        String city = "Green Bay";

        Set<String> emails = Set.of("johndoe@example.com", "janedoe@example.com");

        when(personService.getEmailsByCity(city)).thenReturn(emails);

        mockMvc.perform(get("/communityEmail")
                        .param("city",city))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(emails)));
        
        verify(personService, times(1)).getEmailsByCity(city);            
    }
   
}
