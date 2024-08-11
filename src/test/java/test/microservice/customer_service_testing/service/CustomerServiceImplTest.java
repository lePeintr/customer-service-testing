package test.microservice.customer_service_testing.service;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import test.microservice.customer_service_testing.dtos.CustomerDTO;
import test.microservice.customer_service_testing.entities.Customer;
import test.microservice.customer_service_testing.exceptions.CustomerNotFoundException;
import test.microservice.customer_service_testing.exceptions.EmailAlreadyExistException;
import test.microservice.customer_service_testing.mappers.CustomerMapper;
import test.microservice.customer_service_testing.repositories.ICustomerRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {
    @Mock //Au demarage génère moi un mock pour  CustomerMapper
    private CustomerMapper customerMapper;
    @Mock //Au demarrage génère moi un mock pour  CustomerRepository
    //quand il cree une classe qui implemente ICustomer repository,il redefinit toutesles methodes de cet interface
    //mais le contenu des methode est null quand on appelle une methode il retourne null.
    //c'est à moi de dire si j'appelle cette methode de ce mock voila ce que tu doit me retourner
    //En resume: quand j'appelle cette methode x du mock,tu dois me retourner (Mockito.when(objetmock.methode()).thenResult(y))
    private ICustomerRepository customerRepository;
    @InjectMocks // Au demarrage injecte mes mocks généré dans les service CustomerServiceImpl
    private CustomerServiceImpl underTest;

    @Test
    public void shouldSaveNewCustomer(){
        //etape 1: definir les variables given et expected utilés par les mocks de la méthode à tester
        CustomerDTO givenCustomerDTO = CustomerDTO.builder().firstName("vinicius").lastName("junior").email("vinijunior@madrid.com").build();
        Customer customer = Customer.builder().firstName("vinicius").lastName("junior").email("vinijunior@madrid.com").build();
        Customer expectedCustomerAfterSave = Customer.builder().id(1L).firstName("vinicius").lastName("junior").email("vinijunior@madrid.com").build();
        CustomerDTO expectedResultCustomerDTOAfterSave= CustomerDTO.builder().id(1L).firstName("vinicius").lastName("junior").email("vinijunior@madrid.com").build();

        //étape 2: Definir les scenarios souhaiter quand j'appelle une méthode d'un objet mock
        Mockito.when(customerRepository.findByEmail(givenCustomerDTO.getEmail())).thenReturn(Optional.empty());
        Mockito.when(customerMapper.fromCustomerDTO(givenCustomerDTO)).thenReturn(customer);
        Mockito.when(customerRepository.save(customer)).thenReturn(expectedCustomerAfterSave);
        Mockito.when(customerMapper.fromCustomer(expectedCustomerAfterSave)).thenReturn(expectedResultCustomerDTOAfterSave);

        //étape 3: Appelle la methode du Service à tester
        CustomerDTO result = underTest.saveNewCustomer(givenCustomerDTO);

        //etape 4: Effectuer le test reelement verification que tout a marché (test proprement dit)
        assertThat(result).isNotNull();
        assertThat(expectedResultCustomerDTOAfterSave).usingRecursiveComparison().isEqualTo(result);
    }

    @Test
    public void shouldNotSaveNewCustomerWithEmailExist(){
        //etape 1: definir les variables given et expected utilés par les mocks de la méthode à tester
        CustomerDTO givenCustomerDTO = CustomerDTO.builder().firstName("vinicius").lastName("junior").email("vinijunior@madrid.com").build();
        Customer customer = Customer.builder().firstName("vinicius").lastName("junior").email("vinijunior@madrid.com").build();

        //étape 2: Definir les scenarios souhaiter quand j'appelle une méthode d'un objet mock
        //Dans ce cas je veux qu'il retourne un customer qui existe deja avec cet email
        Mockito.when(customerRepository.findByEmail(givenCustomerDTO.getEmail())).thenReturn(Optional.of(customer));


        //étape 3&4 directement: Appelle la methode du Service à tester dans le cas ou une exeption doit etre retourner quand on appelle la methode
        // Resume: verifie que l'exception est appelé quand on appelle cette methode du service a testere
        //quandj'appelle le scenario definis precedement je dois avoir une exception qui est une instance de la classe EmailAlreadyExistException
        assertThatThrownBy(()->underTest.saveNewCustomer(givenCustomerDTO)).isInstanceOf(EmailAlreadyExistException.class);

    }

    @Test
    public void shouldGetAllCustomer(){
        //etape 1: definir les variables given et expected utilés par les mocks de la méthode à tester
        List<Customer> customers = List.of(
                Customer.builder().firstName("vinicius").lastName("junior").email("vinijunior@madrid.com").build(),
                Customer.builder().firstName("kylian").lastName("mbappe").email("mbappe@madrid.com").build()
        );

        List<CustomerDTO> expectedCustomersDto = List.of(
                CustomerDTO.builder().firstName("vinicius").lastName("junior").email("vinijunior@madrid.com").build(),
                CustomerDTO.builder().firstName("kylian").lastName("mbappe").email("mbappe@madrid.com").build()
        );

        Mockito.when(customerRepository.findAll()).thenReturn(customers);
        Mockito.when(customerMapper.fromListCustomer(customers)).thenReturn(expectedCustomersDto);

        List<CustomerDTO> result = underTest.getAllCustomers();

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(expectedCustomersDto.size());
        assertThat(result).usingRecursiveComparison().isEqualTo(expectedCustomersDto);

    }

    @Test
    public void shouldFindCustomerById(){
        Long givenCustomerId=1L;
        Customer givenCustomer = Customer.builder().id(1L).firstName("vinicius").lastName("junior").email("vini@madric.com").build();
        CustomerDTO expectedCustomerDTO =  CustomerDTO.builder().id(1L).firstName("vinicius").lastName("junior").email("vini@madric.com").build();

        Mockito.when(customerRepository.findById(givenCustomerId)).thenReturn(Optional.of(givenCustomer));
        Mockito.when(customerMapper.fromCustomer(givenCustomer)).thenReturn(expectedCustomerDTO);

        CustomerDTO result = underTest.findCustomerById(givenCustomerId);

        assertThat(result).isNotNull();
        assertThat(result).usingRecursiveComparison().isEqualTo(expectedCustomerDTO);

    }

    @Test
    public void shouldNotFindCustomerById(){
        Long givenCustomerId=8L;

        Mockito.when(customerRepository.findById(givenCustomerId)).thenReturn(Optional.empty());

        assertThatThrownBy(()->underTest.findCustomerById(givenCustomerId)).isInstanceOf(CustomerNotFoundException.class).hasMessage(null);

    }

    @Test
    public void shouldResearchCustomerByKeyword(){
        String givenKeyword="m";

        List<Customer>expectedCustomers = List.of(
                Customer.builder().id(1L).firstName("mbappe").lastName("junior").email("vini@madric.com").build(),
                Customer.builder().id(1L).firstName("bellingham").lastName("junior").email("vini@madric.com").build(),
                Customer.builder().id(1L).firstName("Modric").lastName("junior").email("vini@madric.com").build()
        );

        List<CustomerDTO>expectedCustomerDTOsList = List.of(
                CustomerDTO.builder().id(1L).firstName("mbappe").lastName("junior").email("vini@madric.com").build(),
                CustomerDTO.builder().id(1L).firstName("bellingham").lastName("junior").email("vini@madric.com").build(),
                CustomerDTO.builder().id(1L).firstName("Modric").lastName("junior").email("vini@madric.com").build()
        );



        Mockito.when(customerRepository.findByFirstNameContainsIgnoreCase(givenKeyword)).thenReturn(expectedCustomers);
        Mockito.when(customerMapper.fromListCustomer(expectedCustomers)).thenReturn(expectedCustomerDTOsList);

        List<CustomerDTO> result = underTest.searchCustomers(givenKeyword);

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(expectedCustomerDTOsList.size());
        assertThat(result).usingRecursiveComparison().isEqualTo(expectedCustomerDTOsList);

    }

    @Test
    public void shouldUpdateCustomer(){
        Long givenCustomerId = 1L;
        CustomerDTO givenCustomerDTO=CustomerDTO.builder().id(1L).firstName("mbappe").lastName("junior").email("vini@madric.com").build();

        Customer givenCustomer = Customer.builder().id(1L).firstName("mbappe").lastName("junior").email("vini@madric.com").build();

        Customer updatedGivenCustomer = Customer.builder().id(1L).firstName("mbappe").lastName("junior").email("vini@madric.com").build();

        CustomerDTO expectedCustomerDTO = CustomerDTO.builder().id(1L).firstName("mbappe").lastName("junior").email("vini@madric.com").build();

        Mockito.when(customerRepository.findById(givenCustomerId)).thenReturn(Optional.of(givenCustomer));
        Mockito.when(customerMapper.fromCustomerDTO(givenCustomerDTO)).thenReturn(givenCustomer);
        Mockito.when(customerRepository.save(givenCustomer)).thenReturn(updatedGivenCustomer);
        Mockito.when(customerMapper.fromCustomer(updatedGivenCustomer)).thenReturn(expectedCustomerDTO);

        CustomerDTO result = underTest.updateCustomer(givenCustomerId,givenCustomerDTO);

        assertThat(result).isNotNull();
        assertThat(expectedCustomerDTO).usingRecursiveComparison().isEqualTo(result);
    }

    @Test
    public void shouldNotUpdateCustomerWithCustomerNotFoundException(){
        Long givenCustomerId = 1L;
        CustomerDTO givenCustomerDTO=CustomerDTO.builder().id(1L).firstName("mbappe").lastName("junior").email("vini@madric.com").build();

        Mockito.when(customerRepository.findById(givenCustomerId)).thenReturn(Optional.empty());

        assertThatThrownBy(()->underTest.updateCustomer(givenCustomerId,givenCustomerDTO)).isInstanceOf(CustomerNotFoundException.class);
    }

    @Test
    public void shouldDeleteCustomerById(){
        Long givenId = 1L;
        Customer givenCustomer = Customer.builder().id(1L).firstName("mbappe").lastName("junior").email("vini@madric.com").build();

        Mockito.when(customerRepository.findById(givenId)).thenReturn(Optional.of(givenCustomer));
        underTest.deleteCustomer(givenId);
        Mockito.verify(customerRepository).deleteById(givenId);
    }

    @Test
    public void shouldNotDeleteCustomerWithCustomerNotFoundException(){
        Long givenId = 1L;

        Mockito.when(customerRepository.findById(givenId)).thenReturn(Optional.empty());

        assertThatThrownBy(()->underTest.deleteCustomer(givenId)).isInstanceOf(CustomerNotFoundException.class);

    }
}