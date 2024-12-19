package com.safetynet.safetynet_alert;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.safetynet.safetynet_alert.model.Datas;
import com.safetynet.safetynet_alert.service.DataService;

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
		try {
			datas = dataService.readData();
			System.out.println(datas);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
