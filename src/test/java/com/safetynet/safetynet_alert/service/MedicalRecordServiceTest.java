package com.safetynet.safetynet_alert.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.safetynet.safetynet_alert.data.DatasTest;
import com.safetynet.safetynet_alert.exception.AlreadyExistsException;
import com.safetynet.safetynet_alert.exception.NotFoundException;
import com.safetynet.safetynet_alert.model.Datas;
import com.safetynet.safetynet_alert.model.MedicalRecord;
import com.safetynet.safetynet_alert.repository.DataRepository;

import dto.PersonFullNameDTO;

@ExtendWith(MockitoExtension.class)
public class MedicalRecordServiceTest {
    private Datas datas;

    @Mock
    private DataRepository dataRepository;

    @InjectMocks
    private MedicalRecordService medicalRecordService;

    @BeforeEach
    void setUp(){
        datas = new Datas();
        datas.setMedicalRecords(new ArrayList<>(List.of(new MedicalRecord( //ArrayList<>(List.of()) car List.of est immuable
            "John","Doe", LocalDate.of(1999,1,1), List.of(), List.of()))));
        when(dataRepository.readData()).thenReturn(datas);
    }

    @Test
    void createMedicalRecordTest(){
        MedicalRecord medicalRecord = new MedicalRecord(
            "Jane", "Smith", LocalDate.of(1985, 6, 15), List.of("penicillin"), List.of(""));
    
        medicalRecordService.createMedicalRecord(medicalRecord);

        assertTrue(datas.getMedicalRecords().contains(medicalRecord));
        verify(dataRepository, times(1)).writeData(datas);
    }

    @Test
    void createMedicalRecordAlreadyExistsTest(){
        MedicalRecord medicalRecord = datas.getMedicalRecords().get(0);
    
        assertThrows(AlreadyExistsException.class, () -> medicalRecordService.createMedicalRecord(medicalRecord));

        verify(dataRepository, never()).writeData(datas); 
    }

    @Test
    void updateMedicalRecordTest(){
        MedicalRecord medicalRecordToUpdate = datas.getMedicalRecords().get(0);
        MedicalRecord updatedMedicalRecord =
            new MedicalRecord("John","Doe", LocalDate.of(1999,1,1), List.of(), List.of("Peanuts"));

        medicalRecordService.updateMedicalRecord(updatedMedicalRecord);

        assertTrue(datas.getMedicalRecords().contains(updatedMedicalRecord));
        assertFalse(datas.getMedicalRecords().contains(medicalRecordToUpdate));

        verify(dataRepository, times(1)).writeData(datas);
    }

    @Test
    void updateMedicalRecordNotFoundTest(){
        MedicalRecord medicalRecord = new MedicalRecord(
            "Not", "Existing", LocalDate.of(1998,1,1), List.of(), List.of("Peanuts"));

        assertThrows(NotFoundException.class, () -> medicalRecordService.updateMedicalRecord(medicalRecord));

        verify(dataRepository, never()).writeData(datas);
    }

    @Test
    void deleteMedicalRecordTest(){
        PersonFullNameDTO personToDelete = new PersonFullNameDTO("John", "Doe");
        
        medicalRecordService.deleteMedicalRecord(personToDelete);

        assertFalse(datas.getMedicalRecords().stream()
                        .anyMatch(medicalRecord -> medicalRecord.getFirstName().equals(personToDelete.firstName())
                                    && medicalRecord.getFirstName().equals(personToDelete.lastName())));

        verify(dataRepository, times(1)).writeData(datas);
    }

    @Test
    void deleteMedicalRecordNotFoundTest(){
        PersonFullNameDTO personToDelete = new PersonFullNameDTO("Not", "Existing");

        assertThrows(NotFoundException.class, () -> medicalRecordService.deleteMedicalRecord(personToDelete));

        verify(dataRepository, never()).writeData(datas);
    }
}
