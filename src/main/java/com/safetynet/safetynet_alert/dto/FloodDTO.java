package com.safetynet.safetynet_alert.dto;

import java.util.Map;
import java.util.Set;

public record FloodDTO(
    int fireStation,
    Map<String, Set<PersonWithMedicalDTO>> homes
) {

}
