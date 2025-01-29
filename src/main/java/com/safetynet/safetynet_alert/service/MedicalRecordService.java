package com.safetynet.safetynet_alert.service;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safetynet.safetynet_alert.dto.PersonFullNameDTO;
import com.safetynet.safetynet_alert.exception.AlreadyExistsException;
import com.safetynet.safetynet_alert.exception.NotFoundException;
import com.safetynet.safetynet_alert.model.Datas;
import com.safetynet.safetynet_alert.model.MedicalRecord;
import com.safetynet.safetynet_alert.repository.DataRepository;

/**
 * Service class for handling operations related to medical records.
 * It provides methods to create, update, and delete medical records.
 */
@Service
public class MedicalRecordService {

    private final Logger logger = LogManager.getLogger(MedicalRecordService.class);
    private final DataRepository dataRepository;

    /**
     * Constructor to inject the DataRepository dependency.
     *
     * @param dataRepository the repository to interact with the data store
     */
    @Autowired
    public MedicalRecordService(DataRepository dataRepository){
        this.dataRepository = dataRepository;
    }

    /**
     * Creates a new medical record if it does not already exist.
     * Throws an {@link AlreadyExistsException} if the record already exists.
     *
     * @param medicalRecordToCreate the medical record to be created
     */
    public void createMedicalRecord(MedicalRecord medicalRecordToCreate){
        logger.debug("Creating medical record ({})", medicalRecordToCreate);

        Datas datas = dataRepository.readData();
        List<MedicalRecord> medicalRecords = datas.getMedicalRecords();
        PersonFullNameDTO personFullNameDTO = new PersonFullNameDTO(medicalRecordToCreate.getFirstName(), medicalRecordToCreate.getLastName());

        if (!personFullNameDTO.existsInMeds(medicalRecords)) {
            medicalRecords.add(medicalRecordToCreate);
            datas.setMedicalRecords(medicalRecords);
            dataRepository.writeData(datas);
            logger.info("Medical record created ({})", medicalRecordToCreate);
        } else {
            logger.error("Medical record already exists({})", medicalRecordToCreate);
            throw new AlreadyExistsException("Medical record already exists (" + personFullNameDTO + ")");
        }
    }

    /**
     * Updates an existing medical record.
     * Throws a {@link NotFoundException} if the medical record is not found.
     *
     * @param updatedMedicalRecord the updated medical record
     */
    public void updateMedicalRecord(MedicalRecord updatedMedicalRecord){
        logger.debug("Updating medical record({})", updatedMedicalRecord);

        Datas datas = dataRepository.readData();
        List<MedicalRecord> medicalRecords = datas.getMedicalRecords();
        PersonFullNameDTO medicalRecordToUpdateDTO = new PersonFullNameDTO(updatedMedicalRecord.getFirstName(), updatedMedicalRecord.getLastName());

        Optional<MedicalRecord> medicalRecordToUpdate = medicalRecordToUpdateDTO.findMedicalRecord(medicalRecords);

        medicalRecordToUpdate.ifPresentOrElse(
                medicalRecord -> {
                    medicalRecords.remove(medicalRecord);
                    medicalRecords.add(updatedMedicalRecord);
                    datas.setMedicalRecords(medicalRecords);
                    dataRepository.writeData(datas);
                    logger.info("Medical record updated ({})", updatedMedicalRecord);
                },
                () -> {
                    logger.error("Medical record not found ({} {})", medicalRecordToUpdateDTO.firstName(), medicalRecordToUpdateDTO.lastName());
                    throw new NotFoundException("Medical record not found (" + medicalRecordToUpdateDTO + ")");
                }
        );
    }

    /**
     * Deletes a medical record based on the provided person full name.
     * Throws a {@link NotFoundException} if the medical record is not found.
     *
     * @param medicalRecordDTO the full name of the person whose medical record is to be deleted
     */
    public void deleteMedicalRecord(PersonFullNameDTO medicalRecordDTO){
        logger.debug("Deleting medical record ({})", medicalRecordDTO);

        Datas datas = dataRepository.readData();
        List<MedicalRecord> medicalRecords = datas.getMedicalRecords();

        Optional<MedicalRecord> medicalRecordToDelete = medicalRecordDTO.findMedicalRecord(medicalRecords);

        medicalRecordToDelete.ifPresentOrElse(
                medicalRecord ->  {
                    medicalRecords.remove(medicalRecord);
                    datas.setMedicalRecords(medicalRecords);
                    dataRepository.writeData(datas);
                    logger.info("Medical record deleted ({})", medicalRecordDTO);
                },
                () -> {
                    logger.error("Medical record not found ({} {})", medicalRecordDTO.firstName(), medicalRecordDTO.lastName());
                    throw new NotFoundException("Medical record not found (" + medicalRecordDTO + ")");
                }
        );
    }
}
