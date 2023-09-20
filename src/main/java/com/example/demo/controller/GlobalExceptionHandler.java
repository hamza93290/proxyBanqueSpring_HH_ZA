package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	 	@ResponseStatus(HttpStatus.BAD_REQUEST)
	    @ExceptionHandler(MethodArgumentNotValidException.class)
	    @ResponseBody
	    public Map<String, String> handleValidationException(MethodArgumentNotValidException ex) {
	        HashMap<String, String> errors = new HashMap<>();
	        ex.getBindingResult().getAllErrors().forEach(e -> {
	            String fieldName = ((FieldError) e).getField();
	            String errorMessage = e.getDefaultMessage();
	            errors.put(fieldName, errorMessage);
	        });

	        return errors;
	    }
}
