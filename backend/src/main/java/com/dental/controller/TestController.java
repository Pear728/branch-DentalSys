package com.dental.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试控制器，用于验证REST API是否正常工作
 */
@RestController
public class TestController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello from TestController!";
    }
}
