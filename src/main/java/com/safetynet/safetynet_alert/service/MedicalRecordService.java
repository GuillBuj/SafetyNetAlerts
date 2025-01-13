package com.safetynet.safetynet_alert.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.safetynet.safetynet_alert.exception.AlreadyExistsException;
import com.safetynet.safetynet_alert.exception.NotFoundException;
import com.safetynet.safetynet_alert.model.Datas;
import com.safetynet.safetynet_alert.model.MedicalRecord;
import com.safetynet.safetynet_alert.repository.DataRepository;

import dto.PersonFullNameDTO;

@Service
public class MedicalRecordService {
    private final Logger logger = LogManager.getLogger(MedicalRecordService.class);
    private DataRepository dataRepository;
    
    @Autowired
    public MedicalRecordService(DataRepository dataRepository){
        this.dataRepository = dataRepository;
    }

    public void createMedicalRecord(MedicalRecord medicalRecordToCreate) throws StreamReadException, DatabindException, IOException{
        logger.info("Creating medical record ({})", medicalRecordToCreate);

        Datas datas = dataRepository.readData();

        List<MedicalRecord> medicalRecords = datas.getMedicalRecords();
        PersonFullNameDTO personFullNameDTO
            = new PersonFullNameDTO(medicalRecordToCreate.getFirstName(), medicalRecordToCreate.getLastName());

        if (!personFullNameDTO.existsInMeds(medicalRecords)) {
            medicalRecords.add(medicalRecordToCreate);
            datas.setMedicalRecords(medicalRecords);
            dataRepository.writeData(datas);
        } else {
            logger.warn("Medical record already exists({})", medicalRecordToCreate);
            throw new AlreadyExistsException("Medical record already exists (" + personFullNameDTO + ")");
        }
    }

    public void deleteMedicalRecord(PersonFullNameDTO medicalRecordDTO) throws StreamReadException, DatabindException, IOException{
        logger.info("Deleting medical record ({})", medicalRecordDTO);

        Datas datas = dataRepository.readData();

        List<MedicalRecord> medicalRecords = datas.getMedicalRecords();
       
        Optional<MedicalRecord> medicalRecordToDelete = medicalRecordDTO.findMedicalRecord(medicalRecords);
        
        medicalRecordToDelete.ifPresentOrElse(
            medicalRecord ->  {
                medicalRecords.remove(medicalRecord);
                datas.setMedicalRecords(medicalRecords);
                try {
                    dataRepository.writeData(datas);
                } catch (IOException e) {
                    logger.error("Failed to save data after deletion", e);
                }
            },
            () -> {
                logger.warn("Medical record not found ({} {})", medicalRecordDTO.firstName(), medicalRecordDTO.lastName());
                throw new NotFoundException("Medical record not found (" + medicalRecordDTO + ")");
            }
        );
    }
}
