package com.dtalks.dtalks.base.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BaseController {
    @GetMapping("/hello")
    public String hello() {
        return "Hello";
    }
}
