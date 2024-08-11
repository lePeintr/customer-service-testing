package test.microservice.customer_service_testing.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import test.microservice.customer_service_testing.dtos.CustomerDTO;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

//@Testcontainers
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class CustomerIntegrationTest {
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    /*@Container
    @ServiceConnection
    private static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:16");
*/
    List<CustomerDTO> expectedCustomerDTOList;
    List<CustomerDTO> expectedListWithKeyword;
    @BeforeEach
    public void setUp(){
        this.expectedCustomerDTOList = new ArrayList<>();
        this.expectedCustomerDTOList.add(CustomerDTO.builder().id(1L).firstName("vini").lastName("junior").email("vini@madrid.com").build());
        this.expectedCustomerDTOList.add(CustomerDTO.builder().id(2L).firstName("kylian").lastName("mbappe").email("mbappe@madrid.com").build());
        this.expectedCustomerDTOList.add(CustomerDTO.builder().id(3L).firstName("rodrygo").lastName("goes").email("rodrygo@madrid.com").build());

        this.expectedListWithKeyword = new ArrayList<>();
        this.expectedListWithKeyword.add(CustomerDTO.builder().id(1L).firstName("vini").lastName("junior").email("vini@madrid.com").build());
        this.expectedListWithKeyword.add(CustomerDTO.builder().id(2L).firstName("kylian").lastName("mbappe").email("mbappe@madrid.com").build());
    }


    @Test
    void shouldGetAllCustomers(){
        ResponseEntity<CustomerDTO[]> response = testRestTemplate.exchange("/api/customers", HttpMethod.GET,null,CustomerDTO[].class);
        assertThat(response.getBody()).isNotNull();
        List<CustomerDTO> content = Arrays.asList(response.getBody());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(content.size()).isEqualTo(3);
        assertThat(content).usingRecursiveComparison().isEqualTo(expectedCustomerDTOList);

    }

    @Test
    void shouldSearchCustomersByFirstName(){
        String keyword = "i";
        ResponseEntity<CustomerDTO[]> response = testRestTemplate.exchange("/api/customers/search?keyword="+keyword,HttpMethod.GET,null,CustomerDTO[].class);
        assertThat(response.getBody()).isNotNull();
        List<CustomerDTO> content = Arrays.asList(response.getBody());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(content.size()).isEqualTo(2);
        //pour avoir expectedListWithKeyword on peut aussi utiliser un stream pour filtrer les donner de expectedCustomerDTOList
        /*      List<CustomerDTO> expected = customers.stream().filter(c ->
              c.getFirstName().toLowerCase().contains(keyword.toLowerCase())).collect(Collectors.toList());*/
        assertThat(content).usingRecursiveComparison().isEqualTo(expectedListWithKeyword);
    }

    @Test
    void shouldGetCustomerById(){
        Long customerId=1L;
        ResponseEntity<CustomerDTO> response = testRestTemplate.exchange("/api/customers/"+customerId,HttpMethod.GET,null,CustomerDTO.class);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(expectedCustomerDTOList.get(0));

    }

    @Test
    void shouldNotGetCustomerById(){
        Long customerId=9L;
        ResponseEntity<CustomerDTO> response = testRestTemplate.exchange("/api/customers/"+customerId,HttpMethod.GET,null,CustomerDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Rollback
    void shouldSaveValidCustomer(){
        CustomerDTO customerDTO =CustomerDTO.builder().firstName("lucas").lastName("modric").email("lucas@madrid.com").build();
        ResponseEntity<CustomerDTO> response = testRestTemplate.exchange("/api/customers",HttpMethod.POST,new HttpEntity<>(customerDTO),CustomerDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).usingRecursiveComparison().ignoringFields("id").isEqualTo(customerDTO);
    }

    @Test
    @Rollback
    void shouldNotSaveInValidCustomer() throws JsonProcessingException {
        CustomerDTO customerDTO =CustomerDTO.builder().firstName("").lastName("").email("").build();
        ResponseEntity<String> response = testRestTemplate.exchange("/api/customers",HttpMethod.POST,new HttpEntity<>(customerDTO),String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        AssertionsForClassTypes.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        System.out.println(response);
        Map<String, ArrayList<String>> errors = objectMapper.readValue(response.getBody(), HashMap.class);
        System.out.println(response.getBody());
        System.out.println(errors);
        System.out.println(errors.keySet());
        assertThat(errors.keySet().size()).isEqualTo(3);
       assertThat(errors.get("firstName").size()).isEqualTo(2); //je dois avoir 2 erreurs dans firstName(champ vide et <4 )
        assertThat(errors.get("lastName").size()).isEqualTo(2);//je dois avoir 2 erreurs dans lastName(champ vide et <4 )
        assertThat(errors.get("email").size()).isEqualTo(2);//je dois avoir 2 erreurs dans email(champ vide et <4 )
    }

    @Test
    @Rollback
    void shouldUpdateValidCustomer(){
        Long customerId = 2L;
        CustomerDTO customerDTO = CustomerDTO.builder()
                .id(2L).firstName("toni").lastName("kross").email("toni@gmail.com").build();
        ResponseEntity<CustomerDTO> response = testRestTemplate.exchange("/api/customers/"+customerId, HttpMethod.PUT, new HttpEntity<>(customerDTO), CustomerDTO.class);
        AssertionsForClassTypes.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        AssertionsForClassTypes.assertThat(response.getBody()).usingRecursiveComparison().ignoringFields("id").isEqualTo(customerDTO);
    }
    @Test
    @Rollback
    void shouldDeleteCustomer(){
        Long customerId = 3L;
        ResponseEntity<String> response = testRestTemplate.exchange("/api/customers/"+customerId, HttpMethod.DELETE, null, String.class);
        AssertionsForClassTypes.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

}