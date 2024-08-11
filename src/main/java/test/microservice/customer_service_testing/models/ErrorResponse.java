package test.microservice.customer_service_testing.models;

import java.util.List;
import java.util.Map;

/**
 * Cette classe ErrorResponse représente une classe POJO pour capturer les champs JSON
 *elle est utile pour la methose saveInvalidCustomer du testIntegration et permet la deserialization de la reponse à la requete
 * elle correspond à la structure de reponse renvoyé par l'API
 * @author Utilisateur
 * @version 1.0
 */
public class ErrorResponse {
    private String timestamp;
    private int status;
    private String error;
    private Map<String, List<String>> errors;
    private String path;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    // Getter
    public Map<String, List<String>> getErrors() {
        return errors;
    }

    // Setter
    public void setErrors(Map<String, List<String>> errors) {
        this.errors = errors;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
