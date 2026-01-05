package Alura.Hackaton.SentimentAPI.client;

import Alura.Hackaton.SentimentAPI.config.DsServiceProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class SentimentClient {

    private static final Logger log = LoggerFactory.getLogger(SentimentClient.class);

    private final RestTemplate restTemplate;
    private final DsServiceProperties props;

    public SentimentClient(RestTemplate restTemplate, DsServiceProperties props) {
        this.restTemplate = restTemplate;
        this.props = props;
    }

    public DsPredictResponse predict(String text) {
        String url = props.getBaseUrl() + props.getPredictPath();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        DsPredictRequest body = new DsPredictRequest(text);
        HttpEntity<DsPredictRequest> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<DsPredictResponse> response =
                    restTemplate.postForEntity(url, entity, DsPredictResponse.class);

            DsPredictResponse respBody = response.getBody();
            if (respBody == null) {
                throw new RuntimeException("DS retornou body=null.");
            }
            return respBody;

        } catch (HttpStatusCodeException ex) {
            String dsBody = ex.getResponseBodyAsString();
            log.error("Erro DS em {} -> HTTP {}: {}", url, ex.getStatusCode(), dsBody);
            throw ex;

        } catch (ResourceAccessException ex) {
            log.error("DS indisponível em {}: {}", url, ex.getMessage());
            throw ex;
        }
    }

    public static class DsPredictRequest {
        private String text;

        public DsPredictRequest() {}
        public DsPredictRequest(String text) { this.text = text; }

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
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