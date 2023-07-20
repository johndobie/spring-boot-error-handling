package com.johndobie.springboot.web.controller;

import com.johndobie.springboot.web.model.EchoModel;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

import javax.validation.Valid;

@RestController
@Validated
public class ErrorController {
    public static final String RUNTIME_ERROR_ENDPOINT = "/error/runtime";
    @PostMapping(value = RUNTIME_ERROR_ENDPOINT)
    public String throwRuntimeError(@RequestBody @Valid EchoModel model) {
        throw new RuntimeException("Runtime Error");
    }
}