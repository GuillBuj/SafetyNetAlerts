package com.safetynet.safetynet_alert.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.safetynet.safetynet_alert.data.DatasTest;
import com.safetynet.safetynet_alert.dto.PersonFullNameDTO;
import com.safetynet.safetynet_alert.model.MedicalRecord;
import com.safetynet.safetynet_alert.repository.DataRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class MedicalRecordControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DataRepository dataRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        dataRepository.writeData(new DatasTest().getDatasOneOfEach());
        objectMapper.registerModule(new JavaTimeModule());//pour pouvoir gérer les LocalDate
    }

    @Test
    public void createMedicalRecordTestOk() throws JsonProcessingException, Exception{
        MedicalRecord medicalRecord =
            new MedicalRecord("John","Doe", LocalDate.of(1999,1,1), List.of(), List.of("Peanuts"));

        mockMvc.perform(post("/medicalRecord")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(medicalRecord)))
                .andExpect(status().isCreated());

        assertTrue(dataRepository.readData().getMedicalRecords().contains(medicalRecord));   
    }

    @Test
    public void createMedicalRecordTestAlreadyExists() throws JsonProcessingException, Exception{
        MedicalRecord medicalRecord =
            new MedicalRecord("Jane","Doe", LocalDate.of(1998,1,1),
                            List.of(), List.of("Pollen","Peanuts"));

        mockMvc.perform(post("/medicalRecord")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(medicalRecord)))
                .andExpect(status().isBadRequest());

        assertFalse(dataRepository.readData().getMedicalRecords().contains(medicalRecord));   
    }


    @Test
    public void updateMedicalRecordTestOk() throws JsonProcessingException, Exception{
        MedicalRecord medicalRecord =
            new MedicalRecord("Jane","Doe", LocalDate.of(1998,1,1),
                            List.of(), List.of("Pollen","Peanuts"));

        mockMvc.perform(put("/medicalRecord")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(medicalRecord)))
                .andExpect(status().isOk());

        assertTrue(dataRepository.readData().getMedicalRecords().contains(medicalRecord));        
    }

    @Test
    public void updateMedicalRecordTestNotFound() throws JsonProcessingException, Exception{
        MedicalRecord medicalRecord =
            new MedicalRecord("Jo","Doe", LocalDate.of(1999,1,1), List.of("Amoxicillin"), List.of("Peanuts"));

        mockMvc.perform(put("/medicalRecord")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(medicalRecord)))
                .andExpect(status().isNotFound());

        assertFalse(dataRepository.readData().getMedicalRecords().contains(medicalRecord));
    }

    @Test
    public void deleteMedicalRecordTestOk() throws JsonProcessingException, Exception{
        PersonFullNameDTO person = new PersonFullNameDTO("Jane", "Doe");

        mockMvc.perform(delete("/medicalRecord")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(person)))
                .andExpect(status().isOk());

        assertFalse(dataRepository.readData().getMedicalRecords().stream()
                    .anyMatch(medicalRecord -> medicalRecord.getFirstName().equals(person.firstName())
                                            && medicalRecord.getLastName().equals(person.lastName())));
    }

    @Test
    public void deleteMedicalRecordTestNotFound() throws JsonProcessingException, Exception{
        PersonFullNameDTO person = new PersonFullNameDTO("John", "Doe");

        mockMvc.perform(delete("/medicalRecord")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(person)))
                .andExpect(status().isNotFound());
    }
}
