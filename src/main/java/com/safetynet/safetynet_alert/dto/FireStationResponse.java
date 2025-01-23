package com.safetynet.safetynet_alert.dto;

import java.util.Set;

public record FireStationResponse(
    Set<FireStationPersonDTO> persons,
    int nbAdults,
    int nbChildren
    ) {
}
