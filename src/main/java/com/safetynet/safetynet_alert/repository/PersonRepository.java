package com.safetynet.safetynet_alert.repository;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.safetynet_alert.model.Person;

/**
 * The `PersonRepository` class is responsible for persisting and loading person data 
 * to and from a JSON file. It utilizes Jackson's `ObjectMapper` for serializing 
 * and deserializing objects and provides an abstraction layer for handling file operations.
 */
@Repository
public class PersonRepository {

    // The path to the data file where person information is stored
    private static final String DATA_FILE_PATH = "src\\main\\resources\\data.json";
    
    private final ObjectMapper objectMapper;

    /**
     * Constructor for the `PersonRepository` class.
     * 
     * @param objectMapper The `ObjectMapper` instance used for serializing and deserializing `Person` objects.
     */
    public PersonRepository(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    /**
     * Saves the given list of `Person` objects to the data file at `DATA_FILE_PATH`. 
     * The list is serialized into JSON format using the `ObjectMapper`.
     * 
     * @param persons The list of `Person` objects to be saved.
     * @throws StreamWriteException If there is an error during writing to the stream.
     * @throws DatabindException If there is an error binding the data during serialization.
     * @throws IOException If there is an error while writing to the file.
     */
    public void savePersons(List<Person> persons) throws StreamWriteException, DatabindException, IOException{
        objectMapper.writeValue(new File(DATA_FILE_PATH), persons);
    }
}
