package com.ricky.blogserver.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class FileUploadController {
    @GetMapping("upload")
    public String func() {
        return "Hello World!";
    }
}
