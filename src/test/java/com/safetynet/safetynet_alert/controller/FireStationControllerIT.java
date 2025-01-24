package com.safetynet.safetynet_alert.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.safetynet_alert.dto.ChildAlertChildDTO;
import com.safetynet.safetynet_alert.dto.ChildAlertResponse;
import com.safetynet.safetynet_alert.dto.FireResponse;
import com.safetynet.safetynet_alert.dto.FireStationPersonDTO;
import com.safetynet.safetynet_alert.dto.FireStationResponse;
import com.safetynet.safetynet_alert.dto.FloodDTO;
import com.safetynet.safetynet_alert.dto.PersonWithMedicalDTO;
import com.safetynet.safetynet_alert.exception.AlreadyExistsException;
import com.safetynet.safetynet_alert.exception.NotFoundException;
import com.safetynet.safetynet_alert.model.FireStation;
import com.safetynet.safetynet_alert.model.Person;
import com.safetynet.safetynet_alert.repository.DataRepository;
import com.safetynet.safetynet_alert.service.FireStationService;

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
        dataRepository.clear();
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
        FireStation fireStation = new FireStation("3 Rory Rd", 8);

        mockMvc.perform(post("/firestation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fireStation)));

        mockMvc.perform(post("/firestation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fireStation)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateFireStationTestOk() throws JsonProcessingException, Exception {
        FireStation fireStationBefore = new FireStation("3 Rory Rd", 8);
        FireStation fireStationAfter = new FireStation("3 Rory Rd", 6);

        mockMvc.perform(post("/firestation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fireStationBefore)));

        mockMvc.perform(put("/firestation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fireStationAfter)))
                .andExpect(status().isOk());

        assertTrue(dataRepository.readData().getFireStations().contains(fireStationAfter));
        assertFalse(dataRepository.readData().getFireStations().contains(fireStationBefore));
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
        int stationNumber = 8;
        FireStation fireStation = new FireStation("3 Rory Rd", stationNumber);

        mockMvc.perform(post("/firestation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fireStation)));

        mockMvc.perform(delete("/firestation")
                .param("stationNumber", String.valueOf(stationNumber)))
                .andExpect(status().isOk());

        assertFalse(dataRepository.readData().getFireStations().stream()
                .anyMatch(fStation -> fStation.getStation() == stationNumber));
    }

    @Test
    public void deleteFireStationTestByNumberNotFound() throws JsonProcessingException, Exception {

        mockMvc.perform(delete("/firestation")
                .param("stationNumber", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteFireStationByAddressTestOk() throws JsonProcessingException, Exception {
        String address = "3 Rory Rd";
        FireStation fireStation = new FireStation(address, 8);

        mockMvc.perform(post("/firestation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fireStation)));
        mockMvc.perform(delete("/firestation")
                .param("address", address))
                .andExpect(status().isOk());

        assertFalse(dataRepository.readData().getFireStations().stream()
                .anyMatch(fStation -> fStation.getAddress() == address));
    }

    @Test
    public void deleteFireStationTestByAddressNotFound() throws JsonProcessingException, Exception {
        String address = "3 Rory Rd";

        mockMvc.perform(delete("/firestation")
                .param("address", address))
                .andExpect(status().isNotFound());
    }

    // @Test
    // public void getFireStationOk() throws JsonProcessingException, Exception {
    // int stationNumber = 1;

    // FireStationResponse response = new FireStationResponse(
    // Set.of(new FireStationPersonDTO("John", "Doe", "123 Main St",
    // "111-111-1111"),
    // new FireStationPersonDTO("Jane", "Doe", "123 Main St", "111-111-5555")),
    // 2,
    // 0);

    // when(fireStationService.getPersonsByStation(anyInt())).thenReturn(response);

    // mockMvc.perform(get("/firestation")
    // .param("stationNumber", "1"))
    // .andExpect(status().isOk())
    // .andExpect(content().json(objectMapper.writeValueAsString(response)));

    // verify(fireStationService, times(1)).getPersonsByStation(stationNumber);
    // }

    // @Test
    // public void phoneAlertTestOk() throws JsonProcessingException, Exception {
    // int stationNumber = 1;

    // Set<String> phones = Set.of("123-456-7890", "111-111-1111");

    // when(fireStationService.getPhoneNumbersByStation(anyInt())).thenReturn(phones);

    // mockMvc.perform(get("/phoneAlert")
    // .param("stationNumber", String.valueOf(stationNumber)))
    // .andExpect(status().isOk())
    // .andExpect(content().json(objectMapper.writeValueAsString(phones)));

    // verify(fireStationService, times(1)).getPhoneNumbersByStation(stationNumber);
    // }

    // @Test
    // public void childAlertTestOk() throws JsonProcessingException, Exception {
    // String address = "3 Rory St";

    // ChildAlertResponse response = new ChildAlertResponse(
    // Set.of(new ChildAlertChildDTO("Eric", "Doe", 10),
    // new ChildAlertChildDTO("Kyle", "Doe", 9)),
    // Set.of(new Person(
    // "John", "Doe", "123 Main St", "Springfield", "99999", "555-555-5555",
    // "john.doe@example.com")));

    // when(fireStationService.getChildrenByAdress(anyString())).thenReturn(response);

    // mockMvc.perform(get("/childAlert")
    // .param("address", address))
    // .andExpect(status().isOk())
    // .andExpect(content().json(objectMapper.writeValueAsString(response)));

    // verify(fireStationService, times(1)).getChildrenByAdress(address);
    // }

    // @Test
    // public void fireTestOk() throws JsonProcessingException, Exception {
    // String address = "3 Rory St";

    // FireResponse response = new FireResponse(
    // Set.of(
    // new PersonWithMedicalDTO("John", "Doe", "111-111-1111", 30, List.of(""),
    // List.of("")),
    // new PersonWithMedicalDTO("Jane", "Doe", "111-111-5555", 28, List.of(""),
    // List.of(""))),
    // 1);

    // when(fireStationService.getPersonsByAddress(anyString())).thenReturn(response);

    // mockMvc.perform(get("/fire")
    // .param("address", address))
    // .andExpect(status().isOk())
    // .andExpect(content().json(objectMapper.writeValueAsString(response)));

    // verify(fireStationService, times(1)).getPersonsByAddress(address);
    // }

    // @Test
    // public void floodTestOk() throws JsonProcessingException, Exception {
    // List<Integer> stationNumbers = List.of(1);

    // List<FloodDTO> floodDTOs = List.of(
    // new FloodDTO(
    // 1,
    // Map.of(
    // "3 Rory St", Set.of(
    // new PersonWithMedicalDTO("John", "Doe", "111-111-1111", 30, List.of(""),
    // List.of("")),
    // new PersonWithMedicalDTO("Jane", "Doe", "111-111-5555", 28, List.of(""),
    // List.of(""))))));

    // when(fireStationService.getHomesByStations(anyList())).thenReturn(floodDTOs);

    // mockMvc.perform(get("/flood")
    // .param("stationNumbers", "1"))
    // .andExpect(status().isOk())
    // .andExpect(content().json(objectMapper.writeValueAsString(floodDTOs)));

    // verify(fireStationService, times(1)).getHomesByStations(stationNumbers);

    // }

}
