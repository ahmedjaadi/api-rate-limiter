package api_rate_limiter.service.impl;

import api_rate_limiter.service.ApiServerSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * This is a simple implementation that forward a request with over HTTP
 * @author Ahmed Ali Rashid
 */

@Service
public class RestApiServerSender implements ApiServerSender {
    private final String apiServiceBaseUrl;
    private final RestTemplate restTemplate;

    public RestApiServerSender(@Value("${api.service.base.url}") String apiServiceBaseUrl, RestTemplateBuilder restTemplateBuilder) {
        this.apiServiceBaseUrl = apiServiceBaseUrl;
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public Object send(String endpoint, String method) {
        String url = "%s%s".formatted(apiServiceBaseUrl, endpoint);
        return this.restTemplate.getForObject(url, String.class);
    }
}
