package com.johndobie.springboot.exception;

import com.johndobie.springboot.exception.model.ErrorModel;
import com.johndobie.springboot.exception.model.ErrorType;
import com.johndobie.springboot.exception.model.ErrorResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
@RestController
@Configuration
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponseModel handleException(MethodArgumentNotValidException e) {
        List<ErrorModel> errorModels = processFieldErrors(e);
        return ErrorResponseModel
                .builder()
                .errors(errorModels)
                .type(ErrorType.VALIDATION.toString())
                .build();
    }

    private List<ErrorModel> processFieldErrors(MethodArgumentNotValidException e) {
        List<ErrorModel> validationErrorModels = new ArrayList<ErrorModel>();
        for (FieldError fieldError : e
                .getBindingResult()
                .getFieldErrors()) {

            String code = fieldError.getCode();
            String source = fieldError.getObjectName() + "/" + fieldError.getField();
            String detail = fieldError.getField() + " " + fieldError.getDefaultMessage();

            ErrorModel validationErrorModel = ErrorModel
                    .builder()
                    .code(code)
                    .source(source)
                    .detail(detail)
                    .build();
            validationErrorModels.add(validationErrorModel);
        }

        return validationErrorModels;
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponseModel handleException(ConstraintViolationException e) {
        List<ErrorModel> validationErrorModels = processConstraintViolations(e);
        return ErrorResponseModel
                .builder()
                .errors(validationErrorModels)
                .type(ErrorType.VALIDATION.toString())
                .build();
    }

    private List<ErrorModel> processConstraintViolations(ConstraintViolationException e) {
        List<ErrorModel> errorModels = new ArrayList<ErrorModel>();
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {

            String code = violation
                    .getConstraintDescriptor()
                    .getAnnotation()
                    .annotationType()
                    .getSimpleName();

            String source = violation
                    .getPropertyPath()
                    .toString();

            String detail = violation.getMessage();

            ErrorModel errorModel = ErrorModel
                    .builder()
                    .code(code)
                    .source(source)
                    .detail(detail)
                    .build();

            errorModels.add(errorModel);
        }
        return errorModels;
    }

    private List<ErrorModel> buildSingleError(String code, String detail, String source) {

        ErrorModel errorModel = ErrorModel
                .builder()
                .code(code)
                .source(source)
                .detail(detail)
                .build();

        List<ErrorModel> errorModels = new ArrayList<ErrorModel>();
        errorModels.add(errorModel);

        return errorModels;
    }

}
