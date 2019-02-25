package ca.mcgill.ecse428.foodme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;


@SpringBootApplication
public class FoodmeApplication {

	public String APIKey = "F5ByVWSif5NWb6w3YYAQjRGOI9Xcg8WKqzBDkPnEl4YDneNpsaKn35YcFEqJyvyV_kUTStuTG2n9-Pi9R7-u9GIkmBQY8LjfNJSrAVEs_K5pGJLCAsWc4N3oxGRgXHYx";

	public static void main(String[] args) {
		SpringApplication.run(FoodmeApplication.class, args);
		
        System.out.println("Press 'Enter' to terminate");
        new Scanner(System.in).nextLine();
        System.out.println("terminating application...");
        System.exit(1);
	}


}

