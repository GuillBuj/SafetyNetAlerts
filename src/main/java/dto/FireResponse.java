package dto;

import java.util.Set;

public record FireResponse(
    Set<FirePersonDTO> persons,
    int stationNumber
    ) {
}
