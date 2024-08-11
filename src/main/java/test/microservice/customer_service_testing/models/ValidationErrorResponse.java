package test.microservice.customer_service_testing.models;

import java.util.List;
import java.util.Map;

/**
 * Cette classe ValidationErrorResponse représente ...
 *
 * @author Utilisateur
 * @version 1.0
 */
public class ValidationErrorResponse {
    private Map<String, List<String>> errors;

    // Getter and Setter
    public Map<String, List<String>> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, List<String>> errors) {
        this.errors = errors;
    }
}
