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
import com.safetynet.safetynet_alert.exception.AlreadyExistsException;
import com.safetynet.safetynet_alert.exception.NotFoundException;
import com.safetynet.safetynet_alert.model.FireStation;
import com.safetynet.safetynet_alert.service.FireStationService;

@SpringBootTest
@AutoConfigureMockMvc
public class FireStationControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FireStationService fireStationService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp(){
        objectMapper = new ObjectMapper();
    }

    @Test
    public void createFireStationTestCreated() throws JsonProcessingException, Exception{
        FireStation fireStation = new FireStation("3 Rory Rd", 8);

        doNothing().when(fireStationService).createFireStation(any(FireStation.class));

        mockMvc.perform(post("/firestation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fireStation)))
                .andExpect(status().isCreated());
        
        verify(fireStationService, times(1)).createFireStation(fireStation);
    }

    @Test
    public void createFireStationTestBadRequestAlreadyExists() throws JsonProcessingException, Exception{
        FireStation fireStation = new FireStation("3 Rory Rd", 8);

        doThrow(new AlreadyExistsException("Fire station already exists"))
            .when(fireStationService).createFireStation(any(FireStation.class));

        mockMvc.perform(post("/firestation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fireStation)))
                .andExpect(status().isBadRequest());
        
        verify(fireStationService, times(1)).createFireStation(fireStation);
    }
    
    @Test
    public void updateFireStationTestOk() throws JsonProcessingException, Exception{
        FireStation fireStation = new FireStation("3 Rory Rd", 8);

        doNothing().when(fireStationService).updateFireStation(any(FireStation.class));
        
        mockMvc.perform(put("/firestation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fireStation)))
                .andExpect(status().isOk());

        verify(fireStationService, times(1)).updateFireStation(fireStation);
    }

    @Test
    public void updateFireStationTestNotFound() throws JsonProcessingException, Exception{
        FireStation fireStation = new FireStation("3 Rory Rd", 8);

        doThrow(new NotFoundException("Fire station not found"))
            .when(fireStationService).updateFireStation(fireStation);

        mockMvc.perform(put("/firestation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fireStation)))
                .andExpect(status().isNotFound());
        
        verify(fireStationService, times(1)).updateFireStation(fireStation);
    }

    @Test
    public void deleteFireStationByNumberTestOk() throws JsonProcessingException, Exception{
        
        doNothing().when(fireStationService).deleteFireStation(any(Integer.class));

        mockMvc.perform(delete("/firestation")
                .param("stationNumber","1"))
                .andExpect(status().isOk());
        
        verify(fireStationService, times(1)).deleteFireStation(1);
    }

    @Test
    public void deleteFireStationByAddressTestOk() throws JsonProcessingException, Exception{
        
        doNothing().when(fireStationService).deleteFireStation(any(Integer.class));

        mockMvc.perform(delete("/firestation")
                .param("address","3 Rory Rd"))
                .andExpect(status().isOk());
        
        verify(fireStationService, times(1)).deleteFireStation("3 Rory Rd");
    }

    @Test
    public void updateFireStationTestByNumberNotFound() throws JsonProcessingException, Exception{
       
        doThrow(new NotFoundException("Fire station not found"))
            .when(fireStationService).deleteFireStation(1);

        mockMvc.perform(delete("/firestation")
            .param("stationNumber","1"))
            .andExpect(status().isNotFound());
        
        verify(fireStationService, times(1)).deleteFireStation(1);
    }

    @Test
    public void updateFireStationTestByAddressNotFound() throws JsonProcessingException, Exception{
       
        String address = "3 Rory Rd";
        
        doThrow(new NotFoundException("Address not found"))
            .when(fireStationService).deleteFireStation(address);

            mockMvc.perform(delete("/firestation")
            .param("address",address))
            .andExpect(status().isNotFound());
        
        verify(fireStationService, times(1)).deleteFireStation(address);
    }
}
