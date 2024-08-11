package test.microservice.customer_service_testing.mappers;

import org.junit.jupiter.api.Test;
import test.microservice.customer_service_testing.dtos.CustomerDTO;
import test.microservice.customer_service_testing.entities.Customer;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


class CustomerMapperTest {

    CustomerMapper underTest = new CustomerMapper();

    @Test
    public void shouldMapCustomerToCustomerDto(){
        Customer givenCustomer = Customer.builder().id(1L).firstName("vini").lastName("junior").email("vini@mardid.com").build();

        CustomerDTO expetedCustomerDTO = CustomerDTO.builder().id(1L).firstName("vini").lastName("junior").email("vini@mardid.com").build();

        CustomerDTO result = underTest.fromCustomer(givenCustomer);

        assertThat(expetedCustomerDTO).isNotNull();
        assertThat(expetedCustomerDTO).usingRecursiveComparison().isEqualTo(result);
        assertThat(result).isNotNull();
    }

    @Test
    public void shouldMapCustomerDTOToCustomer(){
        CustomerDTO givenCustomerDTO = CustomerDTO.builder().id(1L).firstName("vini").lastName("junior").email("vini@mardid.com").build();

        Customer expetedCustomer = Customer.builder().id(1L).firstName("vini").lastName("junior").email("vini@mardid.com").build();

        Customer result = underTest.fromCustomerDTO(givenCustomerDTO);

        assertThat(expetedCustomer).isNotNull();
        assertThat(expetedCustomer).usingRecursiveComparison().isEqualTo(result);
        assertThat(result).isNotNull();
    }

    @Test
    public void shouldMapListCustomerToListCustomerDTO(){

        List<Customer> givenListCustomers = List.of(
                Customer.builder().id(1L).firstName("vini").lastName("junior").email("vini@mardid.com").build(),
                Customer.builder().id(1L).firstName("kylian").lastName("mbappe").email("mbappe@mardid.com").build());

        List<CustomerDTO> extectedListCustomersDTO = List.of(
                CustomerDTO.builder().id(1L).firstName("vini").lastName("junior").email("vini@mardid.com").build(),
                CustomerDTO.builder().id(1L).firstName("kylian").lastName("mbappe").email("mbappe@mardid.com").build());

        List<CustomerDTO> result = underTest.fromListCustomer(givenListCustomers);


        assertThat(extectedListCustomersDTO.size()).isEqualTo(result.size());
        assertThat(extectedListCustomersDTO).usingRecursiveComparison().isEqualTo(result);

    }

    @Test //quand j'appelle la methode avec un objet null le resultat doit etre une exception
    public void shouldNotMapNullCustomerToCustomerDto(){
        Customer givenCustomer = null;
        assertThatThrownBy(()->underTest.fromCustomer(givenCustomer))
                .isInstanceOf(IllegalArgumentException.class);


    }

}