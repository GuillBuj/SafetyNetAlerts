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
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.safetynet.safetynet_alert.dto.PersonByLastNameDTO;
import com.safetynet.safetynet_alert.dto.PersonFullNameDTO;
import com.safetynet.safetynet_alert.exception.AlreadyExistsException;
import com.safetynet.safetynet_alert.exception.NotFoundException;
import com.safetynet.safetynet_alert.model.Datas;
import com.safetynet.safetynet_alert.model.MedicalRecord;
import com.safetynet.safetynet_alert.model.Person;
import com.safetynet.safetynet_alert.repository.DataRepository;

@Service
/**
 * Service class responsible for managing person-related operations, such as 
 * creating, updating, deleting persons, mapping persons to their medical records, 
 * and various utility functions related to person data.
 */
public class PersonService {
    
    private final Logger logger = LogManager.getLogger(FireStationService.class);
    private final DataRepository dataRepository;
    private final MedicalRecordService medicalRecordService;

    @Autowired
    /**
     * Constructor that initializes the PersonService with the given DataRepository.
     * 
     * @param dataRepository The data repository used to access person and medical record data.
     */
    public PersonService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
        this.medicalRecordService = new MedicalRecordService(dataRepository);
    }

    /**
     * Creates a new person if they do not already exist in the data repository.
     * Throws an exception if the person already exists.
     * 
     * @param personToCreate The person object to be created and added to the data repository.
     * @throws AlreadyExistsException if the person already exists in the repository.
     */
    public void createPerson(Person personToCreate){
        logger.debug("Creating person({})", personToCreate);

        Datas datas = dataRepository.readData();

        List<Person> persons = datas.getPersons();
        PersonFullNameDTO personFullNameDTO
            = new PersonFullNameDTO(personToCreate.getFirstName(), personToCreate.getLastName());

        if (!personFullNameDTO.exists(persons)) {
            persons.add(personToCreate);
            datas.setPersons(persons);
            dataRepository.writeData(datas);
            logger.info("Person created ({})", personToCreate);
        } else {
            logger.error("Person already exists({})", personToCreate);
            throw new AlreadyExistsException("Person already exists (" + personFullNameDTO + ")");
        }
    }

    /**
     * Updates an existing person in the data repository.
     * Throws an exception if the person is not found.
     * 
     * @param updatedPerson The person object with updated information.
     * @throws NotFoundException if the person is not found in the repository.
     */
    public void updatePerson(Person updatedPerson){
        logger.debug("Updating person({} {})", updatedPerson.getFirstName(), updatedPerson.getLastName());

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
                    dataRepository.writeData(datas);
                    logger.info("Person updated ({})", personToUpdateFullNameDTO);
                },
                () -> {logger.error("Person not existing({} {})",
                        updatedPerson.getFirstName(), updatedPerson.getLastName());
                        throw new NotFoundException("Person not found (" + personToUpdateFullNameDTO + ")");
                    });
    }

    /**
     * Deletes a person from the data repository. If the person has an associated medical record, 
     * the medical record will also be deleted.
     * 
     * @param personDTO The DTO object containing the full name of the person to be deleted.
     * @throws NotFoundException if the person is not found in the repository.
     */
    public void deletePerson(PersonFullNameDTO personDTO){
        logger.debug("Deleting person({} {})", personDTO.firstName(), personDTO.lastName());

        Datas datas = dataRepository.readData();

        List<Person> persons = datas.getPersons();
        Optional<Person> personToDelete = personDTO.findPerson(persons);

        personToDelete.ifPresentOrElse(
                person -> {
                    persons.remove(person);
                    datas.setPersons(persons);

                    dataRepository.writeData(datas);
                    logger.info("Person deleted ({})", personDTO);

                    List<MedicalRecord> medicalRecords = datas.getMedicalRecords();
                    if(personDTO.existsInMeds(medicalRecords)){
                            medicalRecordService.deleteMedicalRecord(personDTO);
                            logger.info("Medical record deleted for person({})", personDTO);
                    }},
                () -> {
                        logger.error("Person not found({} {})", personDTO.firstName(), personDTO.lastName());
                        throw new NotFoundException("Person not found (" + personDTO + ")");
                });
    }

    /**
     * Maps a set of persons to their corresponding medical records.
     * 
     * @param persons A set of Person objects.
     * @return A map where each person is associated with their corresponding medical record.
     */
    public Map<Person, MedicalRecord> mapPersonToMedicalRecord(Set<Person> persons){
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

    /**
     * Retrieves the medical record for a given person.
     * 
     * @param person The person whose medical record is to be retrieved.
     * @return An Optional containing the medical record of the person, or an empty Optional if not found.
     */
    public Optional<MedicalRecord> getMedicalRecord(Person person){
        Datas datas = dataRepository.readData();
        List<MedicalRecord> medicalRecords = datas.getMedicalRecords();

        return medicalRecords.stream()
                .filter(record -> record.getFirstName().equals(person.getFirstName())
                        && record.getLastName().equals(person.getLastName()))
                .findFirst();
    }

    /**
     * Calculates the age of a person based on their birthdate.
     * 
     * @param birthDate The birthdate of the person.
     * @return The age of the person in years.
     */
    public int getAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    /**
     * Retrieves the age of a person using their medical record.
     * 
     * @param person The person whose age is to be calculated.
     * @return The age of the person in years, or -1 if the person's medical record is not found.
     */
    public int getAge(Person person){
        Optional<MedicalRecord> medicalRecord = getMedicalRecord(person);
        if (medicalRecord.isPresent()) {
            return getAge(medicalRecord.get().getBirthdate());
        } else {
            return -1;
        }
    }

    /**
     * Determines if a person is considered an adult (18 or older).
     * 
     * @param person The person to check.
     * @return true if the person is an adult, false otherwise.
     * @throws IOException if there is an error reading medical records.
     * @throws DatabindException if there is an error binding the medical records.
     * @throws StreamReadException if there is an error reading the medical records stream.
     */
    public boolean isAdult(Person person) throws StreamReadException, DatabindException, IOException {
        return getAge(person) >= 18;
    }

    /**
     * Determines if a person is considered a child (younger than 18).
     * 
     * @param person The person to check.
     * @return true if the person is a child, false otherwise.
     * @throws IOException if there is an error reading medical records.
     * @throws DatabindException if there is an error binding the medical records.
     * @throws StreamReadException if there is an error reading the medical records stream.
     */
    public boolean isChild(Person person) throws StreamReadException, DatabindException, IOException {
        int age = getAge(person);
        return age < 18 && age != -1;
    }

    /**
     * Retrieves a list of persons with a matching last name, along with their medical records.
     * 
     * @param lastName The last name to filter persons by.
     * @return A list of PersonByLastNameDTO objects containing person details and medical information.
     */
    public List<PersonByLastNameDTO> getPersonsByLastName(String lastName){
        logger.info("Filter persons by last name({})", lastName);

        Set<Person> persons = dataRepository.readData().getPersons().stream()
                .filter(person -> person.getLastName().equalsIgnoreCase(lastName))
                .collect(Collectors.toSet());

        Map<Person, MedicalRecord> personsMap = mapPersonToMedicalRecord(persons);

        List<PersonByLastNameDTO> personsDTO = personsMap.entrySet().stream()
                .map(entry -> {
                        return new PersonByLastNameDTO(
                                entry.getKey().getFirstName(),
                                entry.getKey().getLastName(),
                                entry.getKey().getPhone(),
                                getAge(entry.getKey()),
                                entry.getKey().getEmail(),
                                entry.getValue().getMedications(),
                                entry.getValue().getAllergies());
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return personsDTO;
    }

    /**
     * Retrieves a set of emails of persons who reside in a specific city.
     * 
     * @param city The city to filter persons by.
     * @return A set of emails of persons who live in the given city.
     */
    public Set<String> getEmailsByCity(String city){
        logger.debug("Get emails by city({})", city);

        Set<String> emails = dataRepository.readData().getPersons().stream()
                .filter(person -> person.getCity().equalsIgnoreCase(city))
                .map(person -> person.getEmail())
                .collect(Collectors.toSet());

        logger.info("List of email by city ({}) : ", city, emails);
        return emails;
    }
}
