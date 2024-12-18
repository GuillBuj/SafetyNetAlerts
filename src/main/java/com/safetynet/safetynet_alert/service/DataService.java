package com.safetynet.safetynet_alert.service;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.safetynet_alert.model.Datas;

@Service
public class DataService {
    private static final String DATA_FILE_PATH = "src\\main\\resources\\data.json";
    private final ObjectMapper objectMapper;

    public DataService(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    private Datas readData() throws StreamReadException, DatabindException, IOException{
        return objectMapper.readValue(new File(DATA_FILE_PATH), Datas.class);
    }

}
