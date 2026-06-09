package com.aivle.bookapp.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello, Spring Boot!";
    }

    // p.72 @PathVariable 실습
    @GetMapping("/hello/{name}")
    public String hello(@PathVariable String name) {
        return "Hello, " + name + "!";
    }

    // p.73 @RequestParam 실습
    @GetMapping("/greet")
    public String greet(@RequestParam String lang) {
        if ("ko".equals(lang)) {
            return "안녕하세요";
        }
        return "Hello";
    }
}
