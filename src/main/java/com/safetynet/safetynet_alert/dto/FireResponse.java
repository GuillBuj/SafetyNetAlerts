package com.safetynet.safetynet_alert.dto;

import java.util.Set;

public record FireResponse(
    Set<PersonWithMedicalDTO> persons,
    int stationNumber
    ) {
}
