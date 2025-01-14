package com.safetynet.safetynet_alert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.safetynet_alert.model.Datas;
import com.safetynet.safetynet_alert.model.Person;
import com.safetynet.safetynet_alert.repository.DataRepository;
import com.safetynet.safetynet_alert.service.FireStationService;
import com.safetynet.safetynet_alert.service.PersonService;

import dto.PersonFullNameDTO;

@SpringBootApplication
public class SafetynetAlertApplication implements CommandLineRunner {

	private final DataRepository dataRepository;

	@Autowired
	public SafetynetAlertApplication(DataRepository dataRepository) {

		this.dataRepository = dataRepository;
	}

	public static void main(String[] args) {

		SpringApplication.run(SafetynetAlertApplication.class, args);
	}

	@Override
	public void run(String... args) {

		Datas datas = new Datas();
		FireStationService fireStationService = new FireStationService(dataRepository);
		PersonService personService = new PersonService(dataRepository);
		try {
			datas = dataRepository.readData();
			
			//System.out.println(datas.getMedicalRecords());
			System.out.println(datas.getFireStations());
			//dataRepository.writeData(datas);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
