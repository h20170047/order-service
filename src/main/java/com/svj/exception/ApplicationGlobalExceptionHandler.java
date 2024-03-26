package com.svj.exception;

import com.svj.entity.ErrorDTO;
import com.svj.entity.ServiceResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedList;
import java.util.List;

@RestControllerAdvice
public class ApplicationGlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ServiceResponse<?> handleMethodArgsValidation(MethodArgumentNotValidException exception){
        ServiceResponse response= new ServiceResponse();
        List<ErrorDTO> errors= new LinkedList<>();
        for(ObjectError error: exception.getAllErrors()){
            ConstraintViolation validationError= error.unwrap(ConstraintViolation.class);
            errors.add(new ErrorDTO(validationError.getPropertyPath()+" : "+validationError.getMessage()));
        }
        response.setErrors(errors);
        return response;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ServiceResponse<?> handleConstraintValidation(ConstraintViolationException exception){
        ServiceResponse response= new ServiceResponse();
        List<ErrorDTO> errors= new LinkedList<>();
        for(ConstraintViolation error: exception.getConstraintViolations()){
            errors.add(new ErrorDTO(error.getPropertyPath()+" : "+error.getMessage()));
        }
        response.setErrors(errors);
        return response;
    }


}
