package com.example.NodoTest.exception;

import com.example.NodoTest.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Locale;

@ControllerAdvice
public class GlobalException {

    MessageSource messageSource;
    public GlobalException(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(CategoryCodeExists.class)
    public ResponseEntity<ApiResponse<Object>> handleCategoryCodeExists(HttpServletRequest httpServletRequest, Locale locale) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(
                ApiResponse.<Object>builder()
                        .message(messageSource.getMessage("response.category.exists", null, locale))
                        .data(httpServletRequest.getRequestURI())
                        .build()
        );
    }
    @ExceptionHandler(NotFound.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound( HttpServletRequest httpServletRequest, Locale locale) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(
                ApiResponse.<Object>builder()
                        .message(messageSource.getMessage("response.notfound", null, locale))
                        .data(httpServletRequest.getRequestURI())
                        .build()
        );
    }
    @ExceptionHandler(AlreadyExists.class)
    public ResponseEntity<ApiResponse<Object>> handleAlreadyExists(HttpServletRequest httpServletRequest, Locale locale) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(
                ApiResponse.<Object>builder()
                        .message(messageSource.getMessage("response.exists", null, locale))
                        .data(httpServletRequest.getRequestURI())
                        .build()
        );
    }

}
