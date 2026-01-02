package Alura.Hackaton.SentimentAPI.exception;

// Deve ser usado para tratar regras de negócio
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}

