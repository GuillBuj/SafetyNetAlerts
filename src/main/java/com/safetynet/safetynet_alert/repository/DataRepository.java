package com.safetynet.safetynet_alert.repository;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.safetynet_alert.model.Datas;

@Repository
public class DataRepository {

    private static final Logger logger = LogManager.getLogger(DataRepository.class);

    private static final String DATA_FILE_PATH = "src\\main\\resources\\data.json";
    private final ObjectMapper objectMapper;

    public DataRepository(ObjectMapper objectMapper){
        
        this.objectMapper = objectMapper;
    }

    public Datas readData() throws StreamReadException, DatabindException, IOException{
        logger.debug("Read objects from json file");
        return objectMapper.readValue(new File(DATA_FILE_PATH), Datas.class);
    }

    public void writeData(Datas datas) throws StreamWriteException, DatabindException, IOException{
        logger.info("Write objects to json file");
        objectMapper.writeValue(new File(DATA_FILE_PATH), datas);
    }

}
