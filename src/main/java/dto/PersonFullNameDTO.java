package dto;

import java.util.List;
import java.util.Optional;

import com.safetynet.safetynet_alert.model.Person;

public record PersonFullNameDTO(
    String firstName,
    String lastName
) {
    public boolean exists(List<Person> persons){
        return persons.stream()
                    .filter(person -> person.getFirstName().equalsIgnoreCase(firstName)
                            && person.getLastName().equalsIgnoreCase(lastName))
                    .findAny()
                    .isPresent();
    }

    public Optional<Person> findPerson(List<Person> persons){
        return persons.stream()
                    .filter(person -> person.getFirstName().equalsIgnoreCase(firstName)
                            && person.getLastName().equalsIgnoreCase(lastName))
                    .findFirst();  
    }
}
