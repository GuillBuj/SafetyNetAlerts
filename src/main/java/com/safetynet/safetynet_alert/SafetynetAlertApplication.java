package com.safetynet.safetynet_alert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.safetynet.safetynet_alert.model.Datas;
import com.safetynet.safetynet_alert.model.Person;
import com.safetynet.safetynet_alert.service.DataService;
import com.safetynet.safetynet_alert.service.FireStationService;
import com.safetynet.safetynet_alert.service.PersonService;

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
		PersonService personService = new PersonService(dataService);
		try {
			datas = dataService.readData();
			/*System.out.println("- Station 1: " + fireStationService.getPersonsByStation(1));
			/*System.out.println("- Station 1: " + fireStationService.getPersonsByStation(1));
			System.out.println("- Station 2: " + fireStationService.getPersonsByStation(2));
			System.out.println("- Station 3: " + fireStationService.getPersonsByStation(3));
			System.out.println("- Station 4: " + fireStationService.getPersonsByStation(4));*/
			// System.out.println(personService.getChildrenByAdress("1509 Culver St"));
			// System.out.println("- Station 1 phone numbers: " + fireStationService.getPhoneNumbersByStation(1));
			// System.out.println(fireStationService.getPersonsByAddress("892 Downing Ct"));
			// System.out.println(fireStationService.getHomesByStations(new ArrayList<>(List.of(1,4))));
			//System.out.println(personService.getPersonsByLastName("Boyd"));
			System.out.println(personService.getEmailsByCity("Culver"));
			personService.createPerson(new Person());
			dataService.writeData(datas);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
