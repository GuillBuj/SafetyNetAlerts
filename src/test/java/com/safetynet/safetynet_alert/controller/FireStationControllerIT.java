package com.safetynet.safetynet_alert.controller;

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

    //TODO voir si il faut faire les tests avec MethodArgumentNotValidException pour tout

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
            .when(fireStationService).updateFireStation(any(FireStation.class));

        mockMvc.perform(put("/firestation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fireStation)))
                .andExpect(status().isNotFound());
        
        verify(fireStationService, times(1)).updateFireStation(fireStation);
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
            .when(fireStationService).deleteFireStation(anyString());

        mockMvc.perform(delete("/firestation")
                        .param("address",address))
                .andExpect(status().isNotFound());
        
        verify(fireStationService, times(1)).deleteFireStation(address);
    }

    @Test
    public void deleteFireStationByNumberTestOk() throws JsonProcessingException, Exception{
        
        doNothing().when(fireStationService).deleteFireStation(anyInt());

        mockMvc.perform(delete("/firestation")
                .param("stationNumber","1"))
                .andExpect(status().isOk());
        
        verify(fireStationService, times(1)).deleteFireStation(1);
    }

    @Test
    public void deleteFireStationByAddressTestOk() throws JsonProcessingException, Exception{
        
        doNothing().when(fireStationService).deleteFireStation(anyInt());

        mockMvc.perform(delete("/firestation")
                .param("address","3 Rory Rd"))
                .andExpect(status().isOk());
        
        verify(fireStationService, times(1)).deleteFireStation("3 Rory Rd");
    }

    @Test
    public void getFireStationOk() throws JsonProcessingException, Exception{
        int stationNumber = 1;

        FireStationResponse response = new FireStationResponse(
            Set.of(new FireStationPersonDTO("John", "Doe", "123 Main St", "111-111-1111"),
                new FireStationPersonDTO("Jane", "Doe", "123 Main St", "111-111-5555")),
            2,
            0);

        when(fireStationService.getPersonsByStation(anyInt())).thenReturn(response);

        mockMvc.perform(get("/firestation")
                        .param("stationNumber", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        verify(fireStationService, times(1)).getPersonsByStation(stationNumber);
    }



    @Test
    public void phoneAlertTestOk() throws JsonProcessingException, Exception{
        int stationNumber = 1;

        Set<String> phones = Set.of("123-456-7890", "111-111-1111");

        when(fireStationService.getPhoneNumbersByStation(anyInt())).thenReturn(phones);

        mockMvc.perform(get("/phoneAlert")
                        .param("stationNumber", String.valueOf(stationNumber)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(phones)));

        verify(fireStationService, times(1)).getPhoneNumbersByStation(stationNumber);
    }
    
    @Test
    public void childAlertTestOk() throws JsonProcessingException, Exception{
        String address = "3 Rory St";

        ChildAlertResponse response = new ChildAlertResponse(
            Set.of(new ChildAlertChildDTO("Eric", "Doe", 10),
                new ChildAlertChildDTO("Kyle", "Doe", 9)),
            Set.of(new Person(
                "John", "Doe", "123 Main St", "Springfield", "99999", "555-555-5555", "john.doe@example.com")));

        when(fireStationService.getChildrenByAdress(anyString())).thenReturn(response);

        mockMvc.perform(get("/childAlert")
                        .param("address", address))  
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        verify(fireStationService, times(1)).getChildrenByAdress(address);
    }

    @Test
    public void fireTestOk() throws JsonProcessingException, Exception{
        String address = "3 Rory St";

        FireResponse response = new FireResponse(
           Set.of(
            new PersonWithMedicalDTO("John", "Doe", "111-111-1111", 30, List.of(""), List.of("")),
            new PersonWithMedicalDTO("Jane", "Doe", "111-111-5555", 28, List.of(""), List.of(""))
           ) 
        , 1);

        when(fireStationService.getPersonsByAddress(anyString())).thenReturn(response);

        mockMvc.perform(get("/fire")
                        .param("address", address))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        verify(fireStationService, times(1)).getPersonsByAddress(address);
    }

    @Test
    public void floodTestOk() throws JsonProcessingException, Exception{
        List<Integer> stationNumbers = List.of(1);

        List<FloodDTO> floodDTOs = List.of(
            new FloodDTO(
                1,
                Map.of(
                    "3 Rory St", Set.of(
                        new PersonWithMedicalDTO("John", "Doe", "111-111-1111", 30, List.of(""), List.of("")),
                        new PersonWithMedicalDTO("Jane", "Doe", "111-111-5555", 28, List.of(""), List.of(""))
                    )
                )
            )
        );

       when(fireStationService.getHomesByStations(anyList())).thenReturn(floodDTOs);

       mockMvc.perform(get("/flood")
                        .param("stationNumbers","1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(floodDTOs)));

        verify(fireStationService,times(1)).getHomesByStations(stationNumbers);

    }

}

