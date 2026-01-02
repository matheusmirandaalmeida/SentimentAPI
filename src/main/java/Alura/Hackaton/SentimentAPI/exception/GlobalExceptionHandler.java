package Alura.Hackaton.SentimentAPI.exception;

import Alura.Hackaton.SentimentAPI.dto.ErrorResponseDTO;
import Alura.Hackaton.SentimentAPI.dto.ValidationErrorDTO;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400 — erro de validação (DTO com @Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ValidationErrorDTO>> tratarErro400(
            MethodArgumentNotValidException ex) {

        var erros = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(
                        Collectors.toMap(
                                FieldError::getField,
                                FieldError::getDefaultMessage,
                                (msg1, msg2) -> msg1
                        )
                );

        var resposta = erros.entrySet().stream()
                .map(e -> new ValidationErrorDTO(e.getKey(), e.getValue()))
                .toList();

        return ResponseEntity.badRequest().body(resposta);
    }


    // 400 — erro de validação em params (@RequestParam, @PathVariable)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleConstraintViolation(
            ConstraintViolationException ex) {

        return ResponseEntity.badRequest().body(
                new ErrorResponseDTO(
                        "Erro de validação",
                        ex.getMessage(),
                        Instant.now()
                )
        );
    }

    // 400 - conteudo JSON inexistente (sem json no body)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleMissingBody(
            HttpMessageNotReadableException ex) {

        return ResponseEntity.badRequest().body(
                new ErrorResponseDTO(
                        "Corpo da requisição ausente ou inválido",
                        "É obrigatório enviar um JSON no corpo da requisição",
                        Instant.now()
                )
        );
    }

    //405 - methodo não permitido
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException ex) {

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(
                new ErrorResponseDTO(
                        "Método HTTP não permitido",
                        "Método '" + ex.getMethod() + "' não é suportado para este endpoint",
                        Instant.now()
                )
        );
    }


    // 415 - Tipo de conteúdo não suportado ou não declarado
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponseDTO> handleUnsupportedMediaType(
            HttpMediaTypeNotSupportedException ex) {

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(
                new ErrorResponseDTO(
                        "Content-Type não suportado",
                        "Utilize 'application/json' no cabeçalho Content-Type",
                        Instant.now()
                )
        );
    }

    //422 - não é possivel processar conteudo, regra de negócio
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponseDTO> handleBusiness(
            BusinessException ex) {

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                new ErrorResponseDTO(
                        "Regra de negócio violada",
                        ex.getMessage(),
                        Instant.now()
                )
        );
    }


    // 503 — O servidor não consegue processar a requisição no momento (DS)
    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ErrorResponseDTO> handleExternal(
            ExternalServiceException ex) {

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponseDTO(
                        "Serviço de análise de sentimento indisponível no momento",
                        ex.getMessage(),
                        Instant.now()
                )
        );
    }

    // 500 — erro inesperado
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneric(Exception ex) {

        return ResponseEntity.internalServerError().body(
                new ErrorResponseDTO(
                        "Erro interno na aplicação",
                        ex.getMessage(),
                        Instant.now()
                )
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDTO(
                        ex.getMessage(),
                        "Recurso inexistente",
                        Instant.now()
                ));
    }
}
