package dto;

import java.util.Set;

import com.safetynet.safetynet_alert.model.Person;

public record FireStationResponse(Set<Person> persons, int nbAdults, int nbChildren) {

}
