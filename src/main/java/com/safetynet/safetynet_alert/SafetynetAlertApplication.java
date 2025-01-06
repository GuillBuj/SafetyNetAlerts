package com.safetynet.safetynet_alert;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.safetynet.safetynet_alert.model.Datas;
import com.safetynet.safetynet_alert.service.DataService;
import com.safetynet.safetynet_alert.service.FireStationService;

@SpringBootApplication
public class SafetynetAlertApplication implements CommandLineRunner{

	private final DataService dataService;
	
	@Autowired
	public SafetynetAlertApplication(DataService dataService){

		this.dataService = dataService;
	}
	public static void main(String[] args) {

		SpringApplication.run(SafetynetAlertApplication.class, args);
	}

	@Override
	public void run(String... args){

		Datas datas = new Datas();
		FireStationService fireStationService = new FireStationService(dataService);
		try {
			datas = dataService.readData();
			//System.out.println(datas);
			//System.out.println(datas.getFireStations());
			System.out.println("- Station 4 adresses: " + fireStationService.getAdressesByStation(datas.getFireStations(), 4));
			System.out.println("- Station 4 people: " + fireStationService.getPersonsByStation(4));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
