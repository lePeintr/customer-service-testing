package test.microservice.customer_service_testing.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import test.microservice.customer_service_testing.dtos.CustomerDTO;
import test.microservice.customer_service_testing.entities.Customer;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerMapper {
    private ModelMapper modelMapper = new ModelMapper();

    public CustomerDTO fromCustomer(Customer customer) {
        return modelMapper.map(customer, CustomerDTO.class);
    }

    public Customer fromCustomerDTO(CustomerDTO customerDTO) {
        return modelMapper.map(customerDTO, Customer.class);
    }

    public List<CustomerDTO> fromListCustomer(List<Customer> customers){
        return customers.stream().map(c->modelMapper.map(c,CustomerDTO.class)).collect(Collectors.toList());
    }
}