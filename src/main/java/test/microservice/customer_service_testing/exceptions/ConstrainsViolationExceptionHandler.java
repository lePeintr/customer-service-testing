package test.microservice.customer_service_testing.exceptions;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.*;
/**
 * Cette classe ConstrainsViolationExceptionHandler représente la gestion des exception lors de l'ajout d'un customer en
 * fonction de critère fixés sur les champs du customer
 * Si vous voulez capturer et renvoyer des détails spécifiques des erreurs de validation,
 * vous devrez modifier votre gestion des exceptions pour inclure ces détails.
 * Voici un exemple pour renvoyer une réponse d'erreur personnalisée avec les détails de validation
 *
 * @author Utilisateur
 * @version 1.0
 */
@ControllerAdvice
public class ConstrainsViolationExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationErrors(ConstraintViolationException exception) {
        Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
        Map<String, List<String>> errors = new HashMap<>();

        for (ConstraintViolation<?> cv : constraintViolations) {
            String fieldName = cv.getPropertyPath().toString();
            String errorMessage = cv.getMessage();

            errors.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(errorMessage);
        }

        // Return a ResponseEntity with a status of BAD_REQUEST and the error details
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationErrors(MethodArgumentNotValidException exception) {
        Map<String, List<String>> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(fieldError -> {
            String fieldName = fieldError.getField();
            String errorMessage = fieldError.getDefaultMessage();
            errors.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(errorMessage);
        });

        // Return a ResponseEntity with a status of BAD_REQUEST and the error details
        return ResponseEntity.badRequest().body(errors);
    }
}
