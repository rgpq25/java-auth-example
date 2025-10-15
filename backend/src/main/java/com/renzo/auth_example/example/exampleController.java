package com.renzo.auth_example.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/example")
public class exampleController {
    @GetMapping("")
    String getExampleEndpoint() {
        return "Hello World!";
    }
}
