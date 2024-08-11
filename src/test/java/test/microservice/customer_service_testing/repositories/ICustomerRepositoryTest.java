package test.microservice.customer_service_testing.repositories;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import test.microservice.customer_service_testing.entities.Customer;


import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@ActiveProfiles("test")
@DataJpaTest
class ICustomerRepositoryTest {
    @Autowired
    private ICustomerRepository customerRepository;

    @BeforeEach //pour indique que la méthode est executée avant chaque test
    public void setup(){
        System.out.println("-----------------------------"); //voir que la methode setup s'execute pour chaque test
        customerRepository.save(Customer.builder().firstName("vini")
                .lastName("junior").email("vini@mardid.com").build());
        customerRepository.save(Customer.builder().firstName("kylian")
                .lastName("mbappe").email("mbappe@mardid.com").build());
        customerRepository.save(Customer.builder().firstName("rodrygo")
                .lastName("goes").email("rodrygo@mardid.com").build());
        System.out.println("-----------------------------");
    }
    @Test //quand on realise le test spring crée aussi les donnees de l'application du CommandLineRunner et si c'est les meme données cree dans
    //les test on a une duplication de données et cela crée une erreur lors du test Pour resoudre le problème dans la classe d'execution de l'application
    //ajouter @Profile(!test) et @ActiveProfile("test") dans la classe de test
    public void shouldFindCustomerByEmail(){
        String givenEmail="vini@mardid.com";
        Optional<Customer> result =  customerRepository.findByEmail(givenEmail);

        //Assertions permet de vérifier si le teste est passé
        assertThat(result).isPresent();
    }

    @Test //scenario ou l'adresse mail n'existe pas
    public void shouldNotFindCustomerByEmail(){
        String givenEmail="kross@mardid.com";
        Optional<Customer> result =  customerRepository.findByEmail(givenEmail);

        //Assertions permet de vérifier si le teste est passé
        assertThat(result).isEmpty();
    }

    @Test
    public void shouldFindCustomerByFirstName(){
        String keyword="i";
        List<Customer> expectedResult = List.of(
                Customer.builder().firstName("vini").lastName("junior").email("vini@mardid.com").build(),
                Customer.builder().firstName("kylian").lastName("mbappe").email("mbappe@mardid.com").build());
        List<Customer> result = customerRepository.findByFirstNameContainsIgnoreCase(keyword);

        //Assertions permet de vérifier si le teste est passé
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(expectedResult.size());
        //usingRecursiveComparison() permet de comparer chaque attribut de chaque element de chaque tableau
        //Dans ce cas ci ce test ne peut pas passer car dans expected resull je n'ai pas défini un id des custmers donc il vaut null lors de la comparaison
        //mais l'id ne vaut pas null dans la base de donnéés quand le customer est créé
        //ignoringFiels(id) veut dire d'ignorer le champs id lors de la comparaison
        assertThat(result).usingRecursiveComparison().ignoringFields("id").isEqualTo(expectedResult);
    }
}