package Alura.Hackaton.SentimentAPI.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ds.service")
public class DsServiceProperties {
    private String baseUrl;
    private String predictPath;

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getPredictPath() { return predictPath; }
    public void setPredictPath(String predictPath) { this.predictPath = predictPath; }
}

