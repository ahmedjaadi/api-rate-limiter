package api_rate_limiter.api_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api_service/api/v1")
public class ApiServiceHelloWorldController {
    @GetMapping("hello")
    ResponseEntity<String> helloWorld(){
        return ResponseEntity.ok().body("Hello World");
    }
}
