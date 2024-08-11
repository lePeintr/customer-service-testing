package test.microservice.customer_service_testing.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.microservice.customer_service_testing.dtos.CustomerDTO;
import test.microservice.customer_service_testing.entities.Customer;
import test.microservice.customer_service_testing.exceptions.CustomerNotFoundException;
import test.microservice.customer_service_testing.exceptions.EmailAlreadyExistException;
import test.microservice.customer_service_testing.mappers.CustomerMapper;
import test.microservice.customer_service_testing.repositories.ICustomerRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class CustomerServiceImpl implements ICustomerService {

    private ICustomerRepository customerRepository;

    private CustomerMapper mapper ;

    public CustomerServiceImpl(ICustomerRepository customerRepository,CustomerMapper mapper) {
        this.customerRepository = customerRepository;
        this.mapper = mapper;
    }

    @Override
    public CustomerDTO saveNewCustomer(CustomerDTO customerDTO) throws EmailAlreadyExistException {
        log.info(String.format("Saving new Customer => %s", customerDTO.toString()));
        Optional<Customer>byEmail = customerRepository.findByEmail(customerDTO.getEmail());
        if (byEmail.isPresent()) {
            log.info(String.format("This email %s already exist", customerDTO.getEmail()));
            throw new EmailAlreadyExistException();
        }
        Customer customerToSave = mapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepository.save(customerToSave);
        CustomerDTO result = mapper.fromCustomer(savedCustomer);
        return result;
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        List<CustomerDTO> customerDTOList= mapper.fromListCustomer(customers);
        return customerDTOList;
    }

    @Override
    public CustomerDTO findCustomerById(Long id) throws CustomerNotFoundException{
        Optional<Customer> customer = customerRepository.findById(id);
        if(customer.isEmpty())
            throw new CustomerNotFoundException();
        return mapper.fromCustomer(customer.get());
    }

    @Override
    public List<CustomerDTO> searchCustomers(String keyword) {
        List<Customer> customers = customerRepository.findByFirstNameContainsIgnoreCase(keyword);
        return mapper.fromListCustomer(customers);
    }

    @Override
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) throws CustomerNotFoundException{
        Optional<Customer> customer = customerRepository.findById(id);
        if(customer.isEmpty())
            throw new CustomerNotFoundException();
        customerDTO.setId(id);
        Customer customerToUpdate = mapper.fromCustomerDTO(customerDTO);
        Customer updatedCustomer =customerRepository.save(customerToUpdate);

        return mapper.fromCustomer(updatedCustomer);
    }

    @Override
    public void deleteCustomer(Long id) throws CustomerNotFoundException{
        Optional <Customer> customer = customerRepository.findById(id);
        if(customer.isEmpty())
            throw new CustomerNotFoundException();
        customerRepository.deleteById(id);
    }
}
