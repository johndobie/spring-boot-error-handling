package com.johndobie.springboot.web.controller;

import com.johndobie.springboot.web.model.EchoModel;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;

import javax.validation.Valid;

@RestController
@Validated
public class EchoController {

    public static final String ECHO_MODEL_ENDPOINT = "/echo/model";

    @PostMapping(value = ECHO_MODEL_ENDPOINT)
    public EchoModel echoModel(@RequestBody @Valid EchoModel model) {
        return model;
    }
}