package dto;

import java.util.Set;

public record FireResponse(
    Set<PersonWithMedicalDTO> persons,
    int stationNumber
    ) {
}
