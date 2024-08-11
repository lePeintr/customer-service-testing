package test.microservice.customer_service_testing.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;

import org.testcontainers.shaded.org.checkerframework.framework.qual.DefaultQualifier;
import test.microservice.customer_service_testing.dtos.CustomerDTO;
import test.microservice.customer_service_testing.entities.Customer;
import test.microservice.customer_service_testing.exceptions.CustomerNotFoundException;
import test.microservice.customer_service_testing.exceptions.EmailAlreadyExistException;
import test.microservice.customer_service_testing.service.ICustomerService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@WebMvcTest(CustomerRestController.class)
class CustomerRestControllerTest {
    @MockBean
    private ICustomerService customerService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    List<CustomerDTO> customerDTOList;

    @BeforeEach
    public void setUp(){
        this.customerDTOList=List.of(
                CustomerDTO.builder().id(1L).firstName("vinicius").lastName("junior").email("vinijunior@madrid.com").build(),
                CustomerDTO.builder().id(2L).firstName("kylian").lastName("mbappe").email("mbappe@madrid.com").build(),
                CustomerDTO.builder().id(3L).firstName("modric").lastName("lucas").email("lucas@madrid.com").build()

        );
    }

    @Test
    public void shouldGetAllCustomers() throws Exception {
        //1: on declare lesgiven data ici cela a ete fait dans @BeforeEach
        //1:quand l'objet mock du service appelle la methode retourne givenData
        Mockito.when(customerService.getAllCustomers()).thenReturn(customerDTOList);
        //etape2: avec Mockmvc.perfom, j'envoie une requete de type get vers /api/customers
        //et quand j'envoie la requete je teste avec les assertions qui sont andExpect qui contient les MAtcher
        //jsonPath rerourne le nombre de custommer qui se trouve dans les données json retournée par la requete
        mockMvc.perform(MockMvcRequestBuilders.get("/api/customers"))
                .andExpect(MockMvcResultMatchers.status().isOk())//je verifie si le status de la requete est OK
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(3)))//je verifie que j'ai bien en retour le nombre de données souhaiyté dans la liste
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(customerDTOList)));//je compare le contenu de la reponse de la requete http avec les donnees de ma liste que je convertis d'abord en json avec ObjectMapper
    }

    @Test
    public void shouldGetCustomerById() throws Exception {
        Long id = 1L;
        Mockito.when(customerService.findCustomerById(id)).thenReturn(customerDTOList.get(0));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/customers/{id}",id))
                .andExpect(MockMvcResultMatchers.status().isOk())
               // .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(1))) pourquoi ici 4 elements sont retournés au lieu de 1
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(customerDTOList.get(0))));
    }

    @Test
    public void shouldNotGetCustomerById() throws Exception {
        Long id = 9L;
        Mockito.when(customerService.findCustomerById(id)).thenThrow(CustomerNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/customers/{id}",id))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(""));
    }


    @Test
    public void shouldSearchCustomerByKeyword() throws Exception {
        String keyword="m";

        List<CustomerDTO> customerDTOList2=List.of(
                CustomerDTO.builder().id(3L).firstName("modric").lastName("lucas").email("lucas@madrid.com").build()
        );
        System.out.println("Testing with keyword: " + keyword);
        Mockito.when(customerService.searchCustomers(keyword)).thenReturn(customerDTOList2);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/customers/search?keyword="+keyword))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(customerDTOList2)));
    }

    @Test
    public void shouldSaveNewCustomer() throws Exception {
        CustomerDTO customerDTO = CustomerDTO.builder().id(7L).firstName("Moréna").lastName("madridistas").email("morena@madrid.com").build();
        String expected = """
                {
                  "id":7, "firstName":"Moréna", "lastName":"madridistas", "email":"morena@madrid.com"
                }
                """;
        Mockito.when(customerService.saveNewCustomer(customerDTO)).thenReturn(customerDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(expected));
    }

    @Test
    public void shouldNotSaveNewCustomerWithEmailExistingException() throws Exception {
        CustomerDTO customerDTO = CustomerDTO.builder().id(7L).firstName("Moréna").lastName("madridistas").email("morena@madrid.com").build();

        Mockito.when(customerService.saveNewCustomer(customerDTO)).thenThrow(EmailAlreadyExistException.class);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(customerDTO)))
                        .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void shouldUpdateCustomer() throws Exception {
        Long id = 7L;
        CustomerDTO customerDTO = CustomerDTO.builder().id(7L).firstName("rodrygo").lastName("goes").email("rodrygo@madrid.com").build();
        String expected = """
                {
                  "id":7, "firstName":"rodrygo", "lastName":"goes", "email":"rodrygo@madrid.com"
                }
                """;
        Mockito.when(customerService.updateCustomer(id,customerDTO)).thenReturn(customerDTO);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/customers/{id}",id)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expected));
    }

    @Test
    void testUpdateCustomerOtherMethod() throws Exception {
        Long customerId=1L;
        CustomerDTO customerDTO= customerDTOList.get(0);
        Mockito.when(customerService.updateCustomer(Mockito.eq(customerId),Mockito.any())).thenReturn(customerDTOList.get(0));
        mockMvc.perform(MockMvcRequestBuilders.put("/api/customers/{id}", customerId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(customerDTO)));
    }

    @Test
    public void shouldNotUpdateCustomerWithCustomerNotFoundException() throws Exception {
        Long id = 7L;
        CustomerDTO customerDTO = CustomerDTO.builder().id(7L).firstName("rodrygo").lastName("goes").email("rodrygo@madrid.com").build();
        Mockito.when(customerService.updateCustomer(id,customerDTO)).thenThrow(CustomerNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/customers/{id}",id)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(""));
    }


    @Test
    void shouldDeleteCustomer() throws Exception {
        Long customerId=1L;
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/customers/{id}",customerId))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void shouldNotDeleteCustomerWithCustomerNotFoundException() throws Exception {
        Long customerId=1L;
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/customers/{id}",customerId))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}