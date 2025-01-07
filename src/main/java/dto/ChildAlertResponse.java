package dto;

import java.util.Set;

import com.safetynet.safetynet_alert.model.Person;

public record ChildAlertResponse(
    Set<ChildAlertChildDTO> children,
    Set<Person> otherPersons
    ) {
}
