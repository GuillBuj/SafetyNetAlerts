package com.safetynet.safetynet_alert.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.safetynet.safetynet_alert.service.PersonService;

import dto.ChildAlertResponse;

@RestController
public class PersonController {
    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService){
        this.personService=personService;
    }

    @GetMapping("/childAlert")
    public ChildAlertResponse getChildrenByAddress(@RequestParam String address){
        try {
            return personService.getChildrenByAdress(address);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
