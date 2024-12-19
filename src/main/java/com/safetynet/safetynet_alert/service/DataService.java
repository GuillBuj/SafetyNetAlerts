package com.safetynet.safetynet_alert.service;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.safetynet_alert.model.Datas;

@Service
public class DataService {

    private static final Logger logger = LogManager.getLogger(DataService.class);

    private static final String DATA_FILE_PATH = "src\\main\\resources\\data.json";
    private final ObjectMapper objectMapper;

    public DataService(ObjectMapper objectMapper){
        
        this.objectMapper = objectMapper;
    }

    public Datas readData() throws StreamReadException, DatabindException, IOException{
        logger.info("Read objects from json");
        return objectMapper.readValue(new File(DATA_FILE_PATH), Datas.class);
    }

}
