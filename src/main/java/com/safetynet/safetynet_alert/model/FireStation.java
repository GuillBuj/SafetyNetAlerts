package com.safetynet.safetynet_alert.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * The `FireStation` class represents a fire station within the safety net system.
 * It holds information about the station's address and the station number.
 * This class is used to store data related to fire stations and is a part of 
 * the overall data model.
 */
@Data
@AllArgsConstructor
public class FireStation {

    // The address of the fire station
    private String address;

    // The number of the fire station
    private int station;
}

