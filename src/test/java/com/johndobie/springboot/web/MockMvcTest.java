package com.johndobie.springboot.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.johndobie.springboot.exception.model.ErrorModel;
import com.johndobie.springboot.web.controller.EchoController;
import com.johndobie.springboot.exception.model.ErrorResponseModel;
import com.johndobie.springboot.exception.model.ErrorType;
import com.johndobie.springboot.web.model.EchoModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static com.johndobie.springboot.web.controller.EchoController.ECHO_MODEL_ENDPOINT;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EchoController.class)
public class MockMvcTest {

    private static final String TEST_MESSAGE = "Hello, World";
    private static final String LONG_TEST_MESSAGE = "This message is more than 30 characters long ...................................";
    private static final String TEST_NAME = "John Dobie";

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void postShouldReturnDefaultMessage() throws Exception {

        EchoModel echoModel = EchoModel
                .builder()
                .message(TEST_MESSAGE)
                .name(TEST_NAME)
                .build();

        MvcResult mvcResult = mockMvc
                .perform(post(ECHO_MODEL_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getJsonObjectAsString(echoModel)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void postShouldReturnMessageTooLong() throws Exception {

        String TEST_NAME = "John Dobie";
        String LONG_TEST_MESSAGE = "This message is more than 30 characters long";

        EchoModel echoModel = EchoModel
                .builder()
                .message(LONG_TEST_MESSAGE)
                .name(TEST_NAME)
                .build();

        MvcResult mvcResult = mockMvc
                .perform(post(ECHO_MODEL_ENDPOINT)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getJsonObjectAsString(echoModel)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andReturn();

        String responseBody = mvcResult
                .getResponse()
                .getContentAsString();

        ErrorResponseModel errorResponseModel = readJsonAsObject(responseBody, ErrorResponseModel.class);
        assertThat(errorResponseModel
                .getErrors()
                .size()).isOne();
        assertThat(errorResponseModel.type).isEqualTo(ErrorType.VALIDATION.toString());

        ErrorModel errorModel = errorResponseModel
                .getErrors()
                .get(0);
        assertThat(errorModel.getCode()).isEqualTo("Size");
        assertThat(errorModel.getDetail()).isEqualTo("message size must be between 1 and 30");
        assertThat(errorModel.getSource()).isEqualTo("echoModel/message");

    }

    @Test
    public void postShouldReturnNameIsMissingAndMessageIsTooLong() throws Exception {

        EchoModel echoModel = EchoModel
                .builder()
                .message(LONG_TEST_MESSAGE)
                .name(null)
                .build();

        String json = getJsonObjectAsString(echoModel);

        MvcResult mvcResult = mockMvc
                .perform(post(ECHO_MODEL_ENDPOINT)
                        .characterEncoding("utf-8")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andReturn();

        String responseBody = mvcResult
                .getResponse()
                .getContentAsString();

        ErrorResponseModel errorResponseModel = readJsonAsObject(responseBody, ErrorResponseModel.class);
        assertThat(errorResponseModel
                .getErrors().size()).isEqualTo(2);
        assertThat(errorResponseModel.type).isEqualTo(ErrorType.VALIDATION.toString());

        ErrorModel nameErrorModel = errorResponseModel
                .getErrors().stream().filter(x -> x.getSource().equals("echoModel/name")).findFirst().get();
        assertThat(nameErrorModel.getCode()).isEqualTo("NotNull");
        assertThat(nameErrorModel.getDetail()).isEqualTo("name may not be null");

        ErrorModel sizeErrorModel = errorResponseModel
                .getErrors().stream().filter(x -> x.getSource().equals("echoModel/message")).findFirst().get();
        assertThat(sizeErrorModel.getCode()).isEqualTo("Size");
        assertThat(sizeErrorModel.getDetail()).isEqualTo("message size must be between 1 and 30");
    }

    private String getJsonObjectAsString(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T readJsonAsObject(String json, Class<T> targetClass) {
        try {
            return objectMapper.readValue(json, targetClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}

