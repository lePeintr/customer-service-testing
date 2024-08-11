package test.microservice.customer_service_testing.service;

import test.microservice.customer_service_testing.dtos.CustomerDTO;
import test.microservice.customer_service_testing.exceptions.CustomerNotFoundException;
import test.microservice.customer_service_testing.exceptions.EmailAlreadyExistException;

import java.util.List;

public interface ICustomerService {

    CustomerDTO saveNewCustomer(CustomerDTO customerDTO) throws EmailAlreadyExistException;
    List<CustomerDTO> getAllCustomers();
    CustomerDTO findCustomerById(Long id) throws CustomerNotFoundException;
    List<CustomerDTO> searchCustomers(String keyword);
    CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) throws CustomerNotFoundException;
    void deleteCustomer(Long id) throws CustomerNotFoundException;
}
