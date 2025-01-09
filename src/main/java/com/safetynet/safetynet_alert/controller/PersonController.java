package com.safetynet.safetynet_alert.controller;


import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.safetynet.safetynet_alert.model.Person;
import com.safetynet.safetynet_alert.service.PersonService;

import dto.PersonByLastNameDTO;

@RestController
@RequestMapping("/person")
public class PersonController {
    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService){
        this.personService=personService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createPerson(@RequestBody Person person) throws StreamReadException, DatabindException, IOException{
        personService.createPerson(person);
    }
    
    @GetMapping("/personInfoLastName")
    public List<PersonByLastNameDTO> getPersonsByLastName(@RequestParam String lastName) throws StreamReadException, DatabindException, IOException{
        return personService.getPersonsByLastName(lastName);
    }

    @GetMapping("/communityEmail")
    public Set<String> getEmailsByCity(@RequestParam String city) throws StreamReadException, DatabindException, IOException{
        return personService.getEmailsByCity(city);
    }

}
