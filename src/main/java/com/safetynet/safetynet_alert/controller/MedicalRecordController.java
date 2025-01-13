package com.safetynet.safetynet_alert.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.safetynet.safetynet_alert.exception.AlreadyExistsException;
import com.safetynet.safetynet_alert.exception.NotFoundException;
import com.safetynet.safetynet_alert.model.MedicalRecord;
import com.safetynet.safetynet_alert.service.MedicalRecordService;

import dto.PersonFullNameDTO;

@RestController
@RequestMapping("/medicalRecord")
public class MedicalRecordController {
    private final MedicalRecordService medicalRecordService;

    @Autowired
    public MedicalRecordController(MedicalRecordService medicalRecordService){
        this.medicalRecordService = medicalRecordService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createMedicalRecord(@RequestBody MedicalRecord medicalRecord) throws StreamReadException, DatabindException, IOException{
        medicalRecordService.createMedicalRecord(medicalRecord);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteMedicalRecord(@RequestBody PersonFullNameDTO medicalRecord) throws StreamReadException, DatabindException, IOException{
        medicalRecordService.deleteMedicalRecord(medicalRecord);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handlePersonAlreadyExistsException(AlreadyExistsException e){
        return e.getMessage();
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handlePersonNotFound(NotFoundException e){
        return e.getMessage();
    }
}
