package Alura.Hackaton.SentimentAPI.client;


import Alura.Hackaton.SentimentAPI.config.DsServiceProperties;
import Alura.Hackaton.SentimentAPI.dto.SentimentResponseDTO;
import Alura.Hackaton.SentimentAPI.exception.ExternalServiceException;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class SentimentClient {

    private final RestTemplate restTemplate;
    private final DsServiceProperties dsServiceProperties;

    public SentimentClient(RestTemplate restTemplate, DsServiceProperties dsServiceProperties) {
        this.restTemplate = restTemplate;
        this.dsServiceProperties = dsServiceProperties;
    }

    public SentimentResponseDTO predict(String text) {
        String url = dsServiceProperties.getBaseUrl() + dsServiceProperties.getPredictPath();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of("text", text);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<SentimentResponseDTO> response =
                    restTemplate.postForEntity(url, request, SentimentResponseDTO.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new ExternalServiceException("Serviço DS retornou resposta inválida.");
            }
            return response.getBody();

        } catch (RestClientException ex) {
            throw new ExternalServiceException("Falha ao chamar serviço de Data Science: " + ex.getMessage(), ex);
        }
    }

}
