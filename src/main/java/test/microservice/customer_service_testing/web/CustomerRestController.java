package test.microservice.customer_service_testing.web;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import test.microservice.customer_service_testing.dtos.CustomerDTO;
import test.microservice.customer_service_testing.exceptions.EmailAlreadyExistException;
import test.microservice.customer_service_testing.service.ICustomerService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CustomerRestController {

    private ICustomerService customerService;

    public CustomerRestController(ICustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/customers")
    public List<CustomerDTO> getAllCustomers(){
        return customerService.getAllCustomers();
    }

    @GetMapping("/customers/{id}")
    public CustomerDTO getCustomerById(@PathVariable Long id){
        return customerService.findCustomerById(id);
    }

    @GetMapping("/customers/search")
    public List<CustomerDTO> searchCustomers(@RequestParam String keyword){
        return customerService.searchCustomers(keyword);
    }

    @PostMapping("/customers")
    @ResponseStatus(HttpStatus.CREATED) //retourne status 201
    public CustomerDTO saveCustomer(@RequestBody @Valid CustomerDTO customerDTO){
        return customerService.saveNewCustomer(customerDTO);
    }

    @PutMapping("/customers/{id}")
    public CustomerDTO updateCustomer(@PathVariable Long id,@RequestBody CustomerDTO customerDTO){
        return customerService.updateCustomer(id,customerDTO);
    }

    @DeleteMapping("/customers/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomer(@PathVariable Long id){
         customerService.deleteCustomer(id);
    }

/*    @ExceptionHandler(EmailAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)  // Statut 409 Conflict pour un conflit de ressources
    public void handleEmailAlreadyExistException(EmailAlreadyExistException ex) {
        System.out.println("Email existe deja");
    }*/
}
