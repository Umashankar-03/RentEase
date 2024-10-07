package com.RentEase.exception;

import com.RentEase.payload.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionHandlerClass {



    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<ErrorDetails>resourceNotFoundException(
            ResourceNotFound exception,
            WebRequest webRequest

    ){
        ErrorDetails errorDetails = new ErrorDetails(exception.getMessage(),new Date(), webRequest.getDescription(false) );
      return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorDetails>unauthorizedException(
            UnauthorizedException exception,
            WebRequest webRequest
    ){
        ErrorDetails errorDetails = new ErrorDetails(exception.getMessage(), new Date(), webRequest.getDescription(false));
        return new ResponseEntity<>(errorDetails , HttpStatus.UNAUTHORIZED);
    }

@ExceptionHandler(DuplicateResourceException.class)
public ResponseEntity<ErrorDetails>duplicateResourceException(
        DuplicateResourceException exception,
        WebRequest webRequest
){
        ErrorDetails errorDetails = new ErrorDetails(exception.getMessage(), new Date(), webRequest.getDescription(false));
        return new ResponseEntity<>(errorDetails , HttpStatus.CONFLICT);
}

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails>globalExceptionHandler(
            Exception exception,
            WebRequest webRequest
    ){
        ErrorDetails errorDetails = new ErrorDetails(exception.getMessage(),new Date(), webRequest.getDescription(false) );
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String , String>>handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception,
            WebRequest webRequest
    ){
        Map<String, String> response = new HashMap<>();

        exception.getBindingResult().getAllErrors().forEach((error)->{
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            response.put(fieldName , message);
        });
       return  new ResponseEntity<Map<String ,String>>(response,HttpStatus.BAD_REQUEST);
    }


}
