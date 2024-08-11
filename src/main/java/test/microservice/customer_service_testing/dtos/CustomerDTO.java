package test.microservice.customer_service_testing.dtos;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CustomerDTO {
    private Long id;
    @NotEmpty
    @Size(min = 2)
    private String firstName;
    @NotEmpty
    @Size(min = 2)
    private String lastName;
    @NotEmpty
    @Size(min = 5)
    private String email;
}
