package com.safetynet.safetynet_alert.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

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
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.safetynet.safetynet_alert.dto.PersonFullNameDTO;
import com.safetynet.safetynet_alert.exception.AlreadyExistsException;
import com.safetynet.safetynet_alert.exception.NotFoundException;
import com.safetynet.safetynet_alert.model.MedicalRecord;
import com.safetynet.safetynet_alert.service.MedicalRecordService;

@SpringBootTest
@AutoConfigureMockMvc
public class MedicalRecordControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MedicalRecordService medicalRecordService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());//pour pouvoir gérer les LocalDate
    }

    @Test
    public void createMedicalRecordTestOk() throws JsonProcessingException, Exception{
        MedicalRecord medicalRecord =
            new MedicalRecord("John","Doe", LocalDate.of(1999,1,1), List.of(), List.of("Peanuts"));

        doNothing().when(medicalRecordService).createMedicalRecord(any(MedicalRecord.class));

        mockMvc.perform(post("/medicalRecord")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(medicalRecord)))
                .andExpect(status().isCreated());

        verify(medicalRecordService, times(1)).createMedicalRecord(medicalRecord);        
    }

    @Test
    public void createMedicalRecordTestAlreadyExists() throws JsonProcessingException, Exception{
        MedicalRecord medicalRecord =
            new MedicalRecord("John","Doe", LocalDate.of(1999,1,1), List.of(), List.of("Peanuts"));

        doThrow(new AlreadyExistsException("Medical record already exists"))
            .when(medicalRecordService).createMedicalRecord(any(MedicalRecord.class));

        mockMvc.perform(post("/medicalRecord")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(medicalRecord)))
                .andExpect(status().isBadRequest());

        verify(medicalRecordService, times(1)).createMedicalRecord(medicalRecord);        
    }

    @Test
    public void updateMedicalRecordTestOk() throws JsonProcessingException, Exception{
        MedicalRecord medicalRecord =
            new MedicalRecord("John","Doe", LocalDate.of(1999,1,1), List.of(), List.of("Peanuts"));

        doNothing().when(medicalRecordService).updateMedicalRecord(any(MedicalRecord.class));

        mockMvc.perform(put("/medicalRecord")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(medicalRecord)))
                .andExpect(status().isOk());

        verify(medicalRecordService, times(1)).updateMedicalRecord(medicalRecord);        
    }

    @Test
    public void updateMedicalRecordTestNotFound() throws JsonProcessingException, Exception{
        MedicalRecord medicalRecord =
            new MedicalRecord("John","Doe", LocalDate.of(1999,1,1), List.of(), List.of("Peanuts"));

        doThrow(new NotFoundException("Medical record not found"))
            .when(medicalRecordService).updateMedicalRecord(any(MedicalRecord.class));

        mockMvc.perform(put("/medicalRecord")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(medicalRecord)))
                .andExpect(status().isNotFound());

        verify(medicalRecordService, times(1)).updateMedicalRecord(medicalRecord);       
    }

    @Test
    public void deleteMedicalRecordTestOk() throws JsonProcessingException, Exception{
        PersonFullNameDTO person = new PersonFullNameDTO("John", "Doe");

        doNothing().when(medicalRecordService).deleteMedicalRecord(any(PersonFullNameDTO.class));

        mockMvc.perform(delete("/medicalRecord")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(person)))
                .andExpect(status().isOk());

        verify(medicalRecordService, times(1)).deleteMedicalRecord(person);;        
    }

    @Test
    public void deleteMedicalRecordTestNotFound() throws JsonProcessingException, Exception{
        PersonFullNameDTO person = new PersonFullNameDTO("John", "Doe");

        doThrow(new NotFoundException("Medical record not found"))
            .when(medicalRecordService).deleteMedicalRecord(person);

        mockMvc.perform(delete("/medicalRecord")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(person)))
                .andExpect(status().isNotFound());

        verify(medicalRecordService, times(1)).deleteMedicalRecord(person);;        
    }


}
