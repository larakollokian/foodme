package ca.mcgill.ecse428.foodme;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ca.mcgill.ecse428.foodme.service.*;

@SpringBootApplication
public class FoodmeApplication {

String APIKey = "F5ByVWSif5NWb6w3YYAQjRGOI9Xcg8WKqzBDkPnEl4YDneNpsaKn35YcFEqJyvyV_kUTStuTG2n9-Pi9R7-u9GIkmBQY8LjfNJSrAVEs_K5pGJLCAsWc4N3oxGRgXHYx";

	public static void main(String[] args) {
		SpringApplication.run(FoodmeApplication.class, args);
	}

	@Bean
	public AuthenticationService authService() {
		return new AuthenticationService();
	}
}

