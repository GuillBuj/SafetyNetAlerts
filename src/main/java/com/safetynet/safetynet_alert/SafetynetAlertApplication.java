package com.safetynet.safetynet_alert;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.safetynet.safetynet_alert.model.Datas;
import com.safetynet.safetynet_alert.repository.DataRepository;
import com.safetynet.safetynet_alert.service.FireStationService;
import com.safetynet.safetynet_alert.service.PersonService;


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
	
		try {
			datas = dataRepository.readData();
			
			System.out.println(datas.getPersons());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
