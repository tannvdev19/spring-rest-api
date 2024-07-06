package com.tannv.jobhunter.error;

import com.tannv.jobhunter.domain.RestResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(value = IdInvalidException.class)
    public ResponseEntity<RestResponse<Object>> handleException(IdInvalidException exception) {
        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setMessage(exception.getMessage());
        res.setError(IdInvalidException.class.toString());
        return ResponseEntity.badRequest().body(res);
    }
}
