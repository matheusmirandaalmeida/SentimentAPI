package Alura.Hackaton.SentimentAPI.client;

import Alura.Hackaton.SentimentAPI.config.DsServiceProperties;
import Alura.Hackaton.SentimentAPI.dto.DsPredictResponseWrapper;
import Alura.Hackaton.SentimentAPI.exception.ExternalServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class SentimentClient {

    private static final Logger log = LoggerFactory.getLogger(SentimentClient.class);

    private final RestTemplate restTemplate;
    private final DsServiceProperties props;

    public SentimentClient(RestTemplate restTemplate, DsServiceProperties props) {
        this.restTemplate = restTemplate;
        this.props = props;
    }

    public DsPredictResponseWrapper predict(String text) {
        String url = props.getBaseUrl() + props.getPredictPath();

        log.info("📡 Chamando FastAPI: {}", url);
        log.info("📝 Texto para análise: '{}'", text);

        try {
            Map<String, String> requestBody = Map.of("text", text);

            log.debug("📦 Enviando JSON: {}", requestBody);

            DsPredictResponseWrapper response = restTemplate.postForObject(
                    url,
                    requestBody,
                    DsPredictResponseWrapper.class
            );

            log.info("Resposta recebida do FastAPI");

            if (response == null) {
                throw new ExternalServiceException("Resposta vazia do FastAPI");
            }

            log.debug("Label: {}, Score: {}", response.getLabel(), response.getScore());
            return response;

        } catch (Exception e) {
            log.error("Erro ao chamar FastAPI: {}", e.getMessage());

            // Debug adicional
            log.error("URL tentada: {}", url);
            log.error("Request body: {{'text':'{}'}}", text);

            throw new ExternalServiceException(
                    "Falha ao chamar serviço de análise: " + e.getMessage(),
                    e
            );
        }
    }
}