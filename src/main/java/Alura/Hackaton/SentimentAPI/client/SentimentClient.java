package Alura.Hackaton.SentimentAPI.client;

import Alura.Hackaton.SentimentAPI.config.DsServiceProperties;
import Alura.Hackaton.SentimentAPI.exception.ExternalServiceException;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class SentimentClient {

    private final RestTemplate restTemplate;
    private final DsServiceProperties props;

    public SentimentClient(RestTemplate restTemplate, DsServiceProperties props) {
        this.restTemplate = restTemplate;
        this.props = props;
    }

    public DsPredictResponse predict(String text) {
        try{

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

    public static class DsPredictRequest {
        private String text;

        public DsPredictRequest() {}
        public DsPredictRequest(String text) { this.text = text; }

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
    }

    public static class DsPredictResponse {
        private String label;     // "Positive"/"Negative"
        private Double score;     // prob classe positiva
        private Integer label_id; // 1 ou 0

        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }

        public Double getScore() { return score; }
        public void setScore(Double score) { this.score = score; }

        public Integer getLabel_id() { return label_id; }
        public void setLabel_id(Integer label_id) { this.label_id = label_id; }
    }
}
