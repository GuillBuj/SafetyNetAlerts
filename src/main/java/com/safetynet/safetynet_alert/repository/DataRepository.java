package com.safetynet.safetynet_alert.repository;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.safetynet_alert.exception.DataRepositoryException;
import com.safetynet.safetynet_alert.model.Datas;

@Repository
public class DataRepository {

    private static final Logger logger = LogManager.getLogger(DataRepository.class);

    @Value("${data.file.path}") // Chemin du fichier injecté depuis application.properties
    private String dataFilePath;

    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;

    public DataRepository(ObjectMapper objectMapper, ResourceLoader resourceLoader) {
        this.objectMapper = objectMapper;
        this.resourceLoader = resourceLoader;
    }

    public Datas readData() throws DataRepositoryException {
        try {
            logger.debug("Reading data from file: {}", dataFilePath);

            Resource resource = resourceLoader.getResource(dataFilePath);

            logger.debug("Resource exists: {}", resource.exists());
            logger.debug("Resource URI: {}", resource.getURI());

            return objectMapper.readValue(resource.getInputStream(), Datas.class);
        } catch (IOException e) {
            logger.error("Error reading data from file: {}", dataFilePath, e);
            throw new DataRepositoryException("Failed to read data from file");
        }
    }

    public void writeData(Datas datas) throws DataRepositoryException {
        try {
            logger.debug("Writing data to file: {}", dataFilePath);
            Resource resource = resourceLoader.getResource(dataFilePath);
            objectMapper.writeValue(resource.getFile(), datas);
        } catch (IOException e) {
            logger.error("Error writing data to file: {}", dataFilePath, e);
            throw new DataRepositoryException("Failed to write data to file");
        }
    }

    public void clear() {
        Datas datas = new Datas();
        writeData(datas);
    }
}

// @Repository
// public class DataRepository {

// private static final Logger logger =
// LogManager.getLogger(DataRepository.class);

// private static final String DATA_FILE_PATH =
// "src\\main\\resources\\data.json";
// private final ObjectMapper objectMapper;

// public DataRepository(ObjectMapper objectMapper){

// this.objectMapper = objectMapper;
// }

// public Datas readData() throws DataRepositoryException{
// try{
// logger.debug("Read objects from json file");
// return objectMapper.readValue(new File(DATA_FILE_PATH), Datas.class);
// } catch(IOException e){
// logger.error("Error reading data from file");
// throw new DataRepositoryException("Failed to read data from file");
// }
// }

// public void writeData(Datas datas) throws DataRepositoryException{
// try {
// logger.debug("Write objects to json file");
// objectMapper.writeValue(new File(DATA_FILE_PATH), datas);
// } catch (IOException e) {
// logger.error("Error writing data to file");
// throw new DataRepositoryException("Failed to write data to file");
// }
// }
//
// }
