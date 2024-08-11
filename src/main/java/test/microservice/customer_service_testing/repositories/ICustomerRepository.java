package test.microservice.customer_service_testing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import test.microservice.customer_service_testing.entities.Customer;

import java.util.List;
import java.util.Optional;

public interface ICustomerRepository extends JpaRepository<Customer,Long> {

    Optional <Customer> findByEmail(String email);

    List<Customer> findByFirstNameContainsIgnoreCase(String keywords);
}
