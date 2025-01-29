package com.safetynet.safetynet_alert.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.safetynet.safetynet_alert.dto.PersonFullNameDTO;
import com.safetynet.safetynet_alert.model.MedicalRecord;
import com.safetynet.safetynet_alert.service.MedicalRecordService;

/**
 * Controller for managing medical records.
 */
@RestController
@RequestMapping("/medicalRecord")
public class MedicalRecordController {
    private final MedicalRecordService medicalRecordService;

    /**
     * Constructor to initialize MedicalRecordController with MedicalRecordService.
     * 
     * @param medicalRecordService The service handling medical record operations.
     */
    @Autowired
    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    /**
     * Creates a new medical record.
     * 
     * @param medicalRecord The medical record to be created.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        medicalRecordService.createMedicalRecord(medicalRecord);
    }

    /**
     * Updates an existing medical record.
     * 
     * @param medicalRecord The medical record with updated information.
     */
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public void updateMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        medicalRecordService.updateMedicalRecord(medicalRecord);
    }

    /**
     * Deletes a medical record based on the person's full name.
     * 
     * @param medicalRecord The DTO containing the full name of the person whose medical record should be deleted.
     */
    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteMedicalRecord(@RequestBody PersonFullNameDTO medicalRecord) {
        medicalRecordService.deleteMedicalRecord(medicalRecord);
    }
}
