package com.ayush.expense_tracker_system.controller;

import com.ayush.expense_tracker_system.dto.response.ErrorResponse;
import com.ayush.expense_tracker_system.dto.response.HelloRequest;
import com.ayush.expense_tracker_system.dto.response.HelloResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class HelloController {

    @GetMapping("api/hello")
    public HelloResponse hello() {
//        throw new Exception("unknown exception");
//        throw new NullPointerException("null pointer exception not handled locally ");
        throw new ArithmeticException("arithmetic exception  handled locally ");
    }

    @GetMapping("api/customHello")
    public ResponseEntity<HelloResponse> customHello(@Valid @RequestBody HelloRequest helloRequest){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new HelloResponse("from backend " + helloRequest.getMessage()));
    }

    @ExceptionHandler(ArithmeticException.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception){
        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage() + " local exception");
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

}
