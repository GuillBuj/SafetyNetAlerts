package com.safetynet.safetynet_alert.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.safetynet_alert.data.DatasTest;
import com.safetynet.safetynet_alert.dto.ChildAlertChildDTO;
import com.safetynet.safetynet_alert.dto.ChildAlertResponse;
import com.safetynet.safetynet_alert.dto.FireResponse;
import com.safetynet.safetynet_alert.dto.FireStationPersonDTO;
import com.safetynet.safetynet_alert.dto.FireStationResponse;
import com.safetynet.safetynet_alert.dto.FloodDTO;
import com.safetynet.safetynet_alert.dto.PersonWithMedicalDTO;
import com.safetynet.safetynet_alert.model.FireStation;
import com.safetynet.safetynet_alert.model.Person;
import com.safetynet.safetynet_alert.repository.DataRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class FireStationControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DataRepository dataRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        dataRepository.writeData(new DatasTest().getDatasOneOfEach());
    }

    @Test
    public void createFireStationTestCreated() throws JsonProcessingException, Exception {
        FireStation fireStation = new FireStation("3 Rory Rd", 8);

        mockMvc.perform(post("/firestation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fireStation)))
                .andExpect(status().isCreated())
                .andReturn();

        assertTrue(dataRepository.readData().getFireStations().contains(fireStation));
    }

    @Test
    public void createFireStationTestBadRequestAlreadyExists() throws JsonProcessingException, Exception {
        FireStation fireStation = new FireStation("8 SRV St", 5);

        mockMvc.perform(post("/firestation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fireStation)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateFireStationTestOk() throws JsonProcessingException, Exception {
        FireStation fireStationAfter = new FireStation("8 SRV St", 5);

        mockMvc.perform(put("/firestation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fireStationAfter)))
                .andExpect(status().isOk());

        assertTrue(dataRepository.readData().getFireStations().contains(fireStationAfter));
    }

    @Test
    public void updateFireStationTestNotFound() throws JsonProcessingException, Exception {
        FireStation fireStation = new FireStation("3 Rory Rd", 8);

        mockMvc.perform(put("/firestation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fireStation)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteFireStationByNumberTestOk() throws JsonProcessingException, Exception {
        int stationNumber = 2;

        mockMvc.perform(delete("/firestation")
                .param("stationNumber", String.valueOf(stationNumber)))
                .andExpect(status().isOk());

        assertFalse(dataRepository.readData().getFireStations().stream()
                .anyMatch(fStation -> fStation.getStation() == stationNumber));
    }

    @Test
    public void deleteFireStationTestByNumberNotFound() throws JsonProcessingException, Exception {

        mockMvc.perform(delete("/firestation")
                .param("stationNumber", "999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteFireStationByAddressTestOk() throws JsonProcessingException, Exception {
        String address = "8 SRV St";

        mockMvc.perform(delete("/firestation")
                .param("address", address))
                .andExpect(status().isOk());

        assertFalse(dataRepository.readData().getFireStations().stream()
                .anyMatch(fStation -> fStation.getAddress().equals(address)));
    }

    @Test
    public void deleteFireStationTestByAddressNotFound() throws JsonProcessingException, Exception {
        String address = "3 Rory Rd";

        mockMvc.perform(delete("/firestation")
                .param("address", address))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getFireStationOk() throws JsonProcessingException, Exception {
        dataRepository.writeData(new DatasTest().getDatasLarge());
        
        FireStationResponse expectedResponse = 
            new FireStationResponse(
                Set.of(
                    new FireStationPersonDTO("Lucas","Johnson", "101 Cherry St", "123-456-7900"),
                    new FireStationPersonDTO("Sophia","Johnson", "101 Cherry St", "123-456-7901"),
                    new FireStationPersonDTO("Ethan","Johnson", "101 Cherry St", "123-456-7902"),    
                    new FireStationPersonDTO("Olivia","Johnson", "101 Cherry St", "123-456-7903"),
                    new FireStationPersonDTO("Evan","Morris", "606 Elm St", "123-456-7912")),
                3,
                2);

        mockMvc.perform(get("/firestation")
            .param("stationNumber", "1"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    public void phoneAlertTestOk() throws JsonProcessingException, Exception {
        dataRepository.writeData(new DatasTest().getDatasLarge());
        
        Set<String> expectedResponse = Set.of("123-456-7910", "123-456-7911","123-456-7913");

        mockMvc.perform(get("/phoneAlert")
            .param("stationNumber", String.valueOf("3")))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    public void childAlertTestOk() throws JsonProcessingException, Exception {
        dataRepository.writeData(new DatasTest().getDatasLarge());
        
        String address = "101 Cherry St";

        ChildAlertResponse expectedResponse
            = new ChildAlertResponse(
                Set.of(
                    new ChildAlertChildDTO("Olivia", "Johnson", 7),
                    new ChildAlertChildDTO("Ethan", "Johnson", 9)),
                Set.of(
                    new Person("Lucas", "Johnson", address, "Culver", "97451", "123-456-7900", "lucas.johnson@example.com"),
                    new Person("Sophia", "Johnson", address, "Culver", "97451", "123-456-7901", "sophia.johnson@example.com")
                    )
                );

        mockMvc.perform(get("/childAlert")
            .param("address", address))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    public void fireTestOk() throws JsonProcessingException, Exception {
        dataRepository.writeData(new DatasTest().getDatasLarge());
        
        FireResponse expectedResponse = new FireResponse(
            Set.of(
                new PersonWithMedicalDTO("Jackson", "Harris", "123-456-7910", 30, List.of("Aspirin"), List.of("Dust","Milk")),
                new PersonWithMedicalDTO("Harper", "Harris", "123-456-7911", 30, List.of("Metformin", "Lisinopril"), List.of("Eggs", "Wheat"))
            ),
            3);

        mockMvc.perform(get("/fire")
            .param("address", "505 Oak St"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    public void floodTestOk() throws JsonProcessingException, Exception {
        dataRepository.writeData(new DatasTest().getDatasLarge());
        
        List<FloodDTO> expectedResponse = List.of(
                new FloodDTO(3, 
                    Map.of(
                        "505 Oak St",
                        Set.of(
                            new PersonWithMedicalDTO("Jackson", "Harris", "123-456-7910", 30, List.of("Aspirin"), List.of("Dust", "Milk")),
                            new PersonWithMedicalDTO("Harper", "Harris", "123-456-7911", 30, List.of("Metformin", "Lisinopril"), List.of("Eggs", "Wheat"))
                        ),
                        "707 Willow St",
                        Set.of(
                            new PersonWithMedicalDTO("Lily", "Green", "123-456-7913", 32, List.of("Insulin", "Aspirin"), List.of("Peanuts", "Soy"))
                        )
                    )
                ),
                new FloodDTO(4, 
                    Map.of("808 Will St",
                            Set.of(
                                new PersonWithMedicalDTO("James", "Marshall", "123-456-7914", 32, List.of(), List.of())
                                )))); 

        mockMvc.perform(get("/flood")
            .param("stationNumbers", "3,4"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
  }

}
