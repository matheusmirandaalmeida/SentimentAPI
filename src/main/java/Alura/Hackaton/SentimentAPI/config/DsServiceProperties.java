package Alura.Hackaton.SentimentAPI.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "ds.service")
public class DsServiceProperties {
    @NotBlank
    private String baseUrl;

    @NotBlank
    private String predictPath;

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getPredictPath() { return predictPath; }
    public void setPredictPath(String predictPath) { this.predictPath = predictPath; }
}
