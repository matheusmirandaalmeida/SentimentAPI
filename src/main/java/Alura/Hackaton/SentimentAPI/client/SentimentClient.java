package Alura.Hackaton.SentimentAPI.client;

import Alura.Hackaton.SentimentAPI.config.DsServiceProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class SentimentClient {


    private final RestTemplate restTemplate;
    private final DsServiceProperties dsServiceProperties;

    public SentimentClient(RestTemplate restTemplate, DsServiceProperties dsServiceProperties) {
        this.restTemplate = restTemplate;
        this.dsServiceProperties = dsServiceProperties;
    }

}
