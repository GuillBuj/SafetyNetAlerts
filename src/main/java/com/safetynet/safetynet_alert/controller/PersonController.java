package com.safetynet.safetynet_alert.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.safetynet.safetynet_alert.dto.PersonByLastNameDTO;
import com.safetynet.safetynet_alert.dto.PersonFullNameDTO;
import com.safetynet.safetynet_alert.model.Person;
import com.safetynet.safetynet_alert.service.PersonService;

/**
 * Controller for managing person-related operations.
 */
@RestController
@RequestMapping
public class PersonController {
    private final PersonService personService;

    /**
     * Constructor to initialize PersonController with PersonService.
     * 
     * @param personService The service handling person operations.
     */
    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    /**
     * Creates a new person record.
     * 
     * @param person The person to be created.
     */
    @PostMapping("/person")
    @ResponseStatus(HttpStatus.CREATED)
    public void createPerson(@RequestBody Person person) {
        personService.createPerson(person);
    }

    /**
     * Updates an existing person record.
     * 
     * @param person The person with updated information.
     */
    @PutMapping("/person")
    @ResponseStatus(HttpStatus.OK)
    public void updatePerson(@RequestBody Person person) {
        personService.updatePerson(person);
    }

    /**
     * Deletes a person record based on the full name.
     * 
     * @param person The DTO containing the full name of the person to delete.
     */
    @DeleteMapping("/person")
    @ResponseStatus(HttpStatus.OK)
    public void deletePerson(@RequestBody PersonFullNameDTO person) {
        personService.deletePerson(person);
    }

    /**
     * Retrieves a list of persons by last name.
     * 
     * @param lastName The last name to filter persons.
     * @return A list of persons with the given last name.
     */
    @GetMapping("/personInfoLastName")
    public List<PersonByLastNameDTO> getPersonsByLastName(@RequestParam String lastName) {
        return personService.getPersonsByLastName(lastName);
    }

    /**
     * Retrieves a set of email addresses for persons living in a specified city.
     * 
     * @param city The city to filter persons.
     * @return A set of email addresses of persons living in the specified city.
     */
    @GetMapping("/communityEmail")
    public Set<String> getEmailsByCity(@RequestParam String city) {
        return personService.getEmailsByCity(city);
    }
}
