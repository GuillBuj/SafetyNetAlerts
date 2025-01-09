package com.safetynet.safetynet_alert.repository;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.safetynet_alert.model.Person;

@Repository
public class PersonRepository {

    private static final String DATA_FILE_PATH = "src\\main\\resources\\data.json";
    private final ObjectMapper objectMapper;

    public PersonRepository(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    public void savePersons(List<Person> persons) throws StreamWriteException, DatabindException, IOException{
        objectMapper.writeValue(new File(DATA_FILE_PATH), persons);
    }
}
