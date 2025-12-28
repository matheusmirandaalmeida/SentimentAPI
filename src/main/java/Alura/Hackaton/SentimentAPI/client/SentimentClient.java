package Alura.Hackaton.SentimentAPI.client;

import Alura.Hackaton.SentimentAPI.dto.SentimentResponseDTO;
import Alura.Hackaton.SentimentAPI.exception.ExternalServiceException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class SentimentClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String url = "http://localhost:8000/predict";

    public SentimentResponseDTO predict(String text) {

        Map<String, String> body = Map.of("text", text);

        try {
            return restTemplate.postForObject(
                    url,
                    body,
                    SentimentResponseDTO.class
            );

        } catch (RestClientException ex) {
            throw new ExternalServiceException(
                    "Falha ao chamar serviço de Data Science", ex
            );
        }
    }
}
