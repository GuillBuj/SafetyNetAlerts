package com.safetynet.safetynet_alert.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.safetynet.safetynet_alert.exception.PersonAlreadyExistsException;
import com.safetynet.safetynet_alert.exception.PersonNotFoundException;
import com.safetynet.safetynet_alert.model.Datas;
import com.safetynet.safetynet_alert.model.MedicalRecord;
import com.safetynet.safetynet_alert.model.Person;
import com.safetynet.safetynet_alert.repository.DataRepository;

import dto.PersonByLastNameDTO;
import dto.PersonFullNameDTO;

@Service
public class PersonService {
    private final Logger logger = LogManager.getLogger(FireStationService.class);
    private final DataRepository dataRepository;

    @Autowired
    public PersonService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    public void createPerson(Person personToCreate) throws StreamReadException, DatabindException, IOException {
        logger.info("Creating person({})", personToCreate);

        Datas datas = dataRepository.readData();

        List<Person> persons = datas.getPersons();
        PersonFullNameDTO personFullNameDTO
            = new PersonFullNameDTO(personToCreate.getFirstName(), personToCreate.getLastName());

        if (!personFullNameDTO.exists(persons)) {
            persons.add(personToCreate);
            datas.setPersons(persons);
            dataRepository.writeData(datas);
        } else {
            logger.warn("Person already exists({})", personToCreate);
            throw new PersonAlreadyExistsException("Person already exists (" + personFullNameDTO + ")");
        }
    }

    public void updatePerson(Person updatedPerson) throws StreamReadException, DatabindException, IOException {
        logger.info("Updating person({} {})", updatedPerson.getFirstName(), updatedPerson.getLastName());

        Datas datas = dataRepository.readData();

        List<Person> persons = datas.getPersons();
        PersonFullNameDTO personToUpdateFullNameDTO
            = new PersonFullNameDTO(updatedPerson.getFirstName(), updatedPerson.getLastName());

        Optional<Person> personToUpdate = personToUpdateFullNameDTO.findPerson(persons);

        personToUpdate.ifPresentOrElse(
                person -> {
                    persons.remove(person);
                    persons.add(updatedPerson);
                    datas.setPersons(persons);
                    try {
                        dataRepository.writeData(datas);
                    } catch (IOException e) {
                        logger.error("Failed to save data after update", e);
                    }
                },
                () -> {logger.warn("Person not existing({} {})",
                        updatedPerson.getFirstName(), updatedPerson.getLastName());
                        throw new PersonNotFoundException("Person not found (" + personToUpdateFullNameDTO + ")");
                    });
    }

    public void deletePerson(PersonFullNameDTO personDTO) throws StreamReadException, DatabindException, IOException {
        logger.info("Deleting person({} {})", personDTO.firstName(), personDTO.lastName());

        Datas datas = dataRepository.readData();

        List<Person> persons = datas.getPersons();
        PersonFullNameDTO personFullNameDTO
            = new PersonFullNameDTO(personDTO.firstName(), personDTO.lastName());

        Optional<Person> personToDelete = personFullNameDTO.findPerson(persons);

        personToDelete.ifPresentOrElse(
                person -> {
                    persons.remove(person);
                    // TODO delete medrecord
                    datas.setPersons(persons);
                    try {
                        dataRepository.writeData(datas);
                    } catch (IOException e) {
                        logger.error("Failed to save data after deletion", e);
                    }
                },
                () -> {logger.warn("Person not existing({} {})",
                            personDTO.firstName(), personDTO.lastName());
                        throw new PersonNotFoundException("Person not found (" + personFullNameDTO + ")");
                    });
    }

    public Map<Person, MedicalRecord> mapPersonToMedicalRecord(Set<Person> persons)
            throws StreamReadException, DatabindException, IOException {
        Datas datas = dataRepository.readData();
        List<MedicalRecord> medicalRecords = datas.getMedicalRecords();
        Map<Person, MedicalRecord> mapPersonMedicalReport = new HashMap<>();

        for (Person person : persons) {
            Optional<MedicalRecord> medicalRecord = medicalRecords.stream()
                    .filter(record -> record.getFirstName().equals(person.getFirstName())
                            && record.getLastName().equals(record.getLastName()))
                    .findFirst();

            if (medicalRecord.isPresent()) {
                mapPersonMedicalReport.put(person, medicalRecord.get());
            } else {
                logger.warn("No medical record(" + person.getFirstName() + " " + person.getLastName() + ")");
            }
        }

        return mapPersonMedicalReport;
    }

    public Optional<MedicalRecord> getMedicalRecord(Person person)
            throws StreamReadException, DatabindException, IOException {
        Datas datas = dataRepository.readData();
        List<MedicalRecord> medicalRecords = datas.getMedicalRecords();

        return medicalRecords.stream()
                .filter(record -> record.getFirstName().equals(person.getFirstName())
                        && record.getLastName().equals(person.getLastName()))
                .findFirst();
    }

    public int getAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public int getAge(Person person) throws StreamReadException, DatabindException, IOException {
        Optional<MedicalRecord> medicalRecord = getMedicalRecord(person);
        if (medicalRecord.isPresent()) {
            return getAge(medicalRecord.get().getBirthdate());
        } else {
            return -1;
        }
    }

    public boolean isAdult(Person person) throws StreamReadException, DatabindException, IOException {
        return getAge(person) >= 18;
    }

    public boolean isChild(Person person) throws StreamReadException, DatabindException, IOException {
        int age = getAge(person);
        return age < 18 && age != -1;
    }

    public List<PersonByLastNameDTO> getPersonsByLastName(String lastName)
            throws StreamReadException, DatabindException, IOException {
        logger.info("Filter persons by last name({})", lastName);

        Set<Person> persons = dataRepository.readData().getPersons().stream()
                .filter(person -> person.getLastName().equalsIgnoreCase(lastName)) // ignorecase pour que ça fonctionne
                .collect(Collectors.toSet());

        Map<Person, MedicalRecord> personsMap = mapPersonToMedicalRecord(persons);

        List<PersonByLastNameDTO> personsDTO = personsMap.entrySet().stream()
                .map(entry -> {
                    try {
                        return new PersonByLastNameDTO(
                                entry.getKey().getFirstName(),
                                entry.getKey().getLastName(),
                                entry.getKey().getPhone(),
                                getAge(entry.getKey()),
                                entry.getKey().getEmail(),
                                entry.getValue().getMedications(),
                                entry.getValue().getAllergies());
                    } catch (IOException e) {
                        logger.error("Error processing {} {}", entry.getKey().getFirstName(),
                                entry.getKey().getLastName());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return personsDTO;
    }

    public Set<String> getEmailsByCity(String city) throws StreamReadException, DatabindException, IOException {
        logger.info("Get emails by city({})", city);

        Set<String> emails = dataRepository.readData().getPersons().stream()
                .filter(person -> person.getCity().equalsIgnoreCase(city))
                .map(person -> person.getEmail())
                .collect(Collectors.toSet());

        return emails;
    }

}
