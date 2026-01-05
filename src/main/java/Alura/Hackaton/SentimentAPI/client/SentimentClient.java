package Alura.Hackaton.SentimentAPI.client;

import Alura.Hackaton.SentimentAPI.config.DsServiceProperties;
<<<<<<< Updated upstream
import Alura.Hackaton.SentimentAPI.exception.ExternalServiceException;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
=======
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
>>>>>>> Stashed changes
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class SentimentClient {

    private final RestTemplate restTemplate;
    private final DsServiceProperties props;
    private final ObjectMapper objectMapper;

    public SentimentClient(RestTemplate restTemplate, DsServiceProperties props, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.props = props;
        this.objectMapper = objectMapper;
    }

    public DsPredictResponse predict(String text) {
        try{

<<<<<<< Updated upstream
            String url = props.getBaseUrl() + props.getPredictPath();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            DsPredictRequest body = new DsPredictRequest(text);
            HttpEntity<DsPredictRequest> entity = new HttpEntity<>(body, headers);

            ResponseEntity<DsPredictResponse> response =
                    restTemplate.postForEntity(url, entity, DsPredictResponse.class);

            return response.getBody();
        } catch (RestClientException ex){
            throw new ExternalServiceException(
                    "Erro ao chamar o serviço de DataScience",
                    ex
            );
        }

    }
=======
        try {
            String json = objectMapper.writeValueAsString(Map.of("text", text));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

            HttpEntity<String> entity = new HttpEntity<>(json, headers);

            ResponseEntity<DsPredictResponse> resp =
                    restTemplate.exchange(url, HttpMethod.POST, entity, DsPredictResponse.class);
>>>>>>> Stashed changes

            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
                throw new RuntimeException("DS retornou " + resp.getStatusCode());
            }

            return resp.getBody();

        } catch (HttpStatusCodeException ex) {
            throw new RuntimeException(
                    "Erro chamando DS em " + url +
                            " | status=" + ex.getStatusCode() +
                            " | body=" + ex.getResponseBodyAsString(),
                    ex
            );
        } catch (Exception ex) {
            throw new RuntimeException("Falha chamando DS em " + url + ": " + ex.getMessage(), ex);
        }
    }

    public static class DsPredictResponse {
        private String label;
        private Double score;

        @JsonProperty("label_id")
        private Integer labelId;

        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }

        public Double getScore() { return score; }
        public void setScore(Double score) { this.score = score; }

        public Integer getLabelId() { return labelId; }
        public void setLabelId(Integer labelId) { this.labelId = labelId; }
    }
}
