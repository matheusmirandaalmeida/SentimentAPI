package Alura.Hackaton.SentimentAPI.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Para arquivos HTML na pasta html/
        registry.addResourceHandler("/html/**")
                .addResourceLocations("classpath:/static/html/");

        // Para CSS
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");

        // Para JS
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");

        // Para imagens
        registry.addResourceHandler("/img/**")
                .addResourceLocations("classpath:/static/img/");

        // Para acessar arquivos diretamente pelo nome
        registry.addResourceHandler("/*.html")
                .addResourceLocations("classpath:/static/html/");

        // Para acessar pela raiz
        registry.addResourceHandler("/")
                .addResourceLocations("classpath:/static/html/landing.html");
    }
}