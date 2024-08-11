package test.microservice.customer_service_testing;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import test.microservice.customer_service_testing.entities.Customer;
import test.microservice.customer_service_testing.repositories.ICustomerRepository;

@SpringBootApplication
public class CustomerServiceTestingApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerServiceTestingApplication.class, args);
	}

	@Bean
	//@Profile("!test") //eviter d'executer cette methode quand on lance les tests
	CommandLineRunner commandLineRunner(ICustomerRepository customerRepository){
		return args->{
			customerRepository.save(Customer.builder().firstName("vini")
					.lastName("junior").email("vini@madrid.com").build());
			customerRepository.save(Customer.builder().firstName("kylian")
					.lastName("mbappe").email("mbappe@madrid.com").build());
			customerRepository.save(Customer.builder().firstName("rodrygo")
					.lastName("goes").email("rodrygo@madrid.com").build());
		};
	}
}
