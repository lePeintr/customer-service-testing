package test.microservice.customer_service_testing;

import org.springframework.boot.SpringApplication;

public class TestCustomerServiceTestingApplication {

	public static void main(String[] args) {
		SpringApplication.from(CustomerServiceTestingApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
