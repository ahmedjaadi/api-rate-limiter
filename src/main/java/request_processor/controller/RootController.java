package request_processor.controller;

import api_rate_limiter.exception.api_error.ApiError;
import api_rate_limiter.service.RateLimitAlgorithm;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {
    private final RateLimitAlgorithm rateLimitAlgorithm;

    public RootController(RateLimitAlgorithm rateLimitAlgorithm) {
        this.rateLimitAlgorithm = rateLimitAlgorithm;
    }

    @GetMapping(path = "/hello")
    public ResponseEntity<String> helloWorld(@Param("tokens") int tokens) {
        if (rateLimitAlgorithm.tryConsume(tokens)) {
            return this.forwardToServer();
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Too many request");
    }

    @GetMapping(path = "/error")
    public ResponseEntity<ApiError> helloHandler() {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(HttpStatus.BAD_REQUEST.value(), "The application accepts only HTTP GET request whose url matches with /api/v1/**"));
    }

    private ResponseEntity<String> forwardToServer() {
        return ResponseEntity.ok().body("Hello World");
    }

}
