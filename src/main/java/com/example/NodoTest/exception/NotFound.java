package com.example.NodoTest.exception;

public class NotFound extends RuntimeException {
    public NotFound() {
        super("response.notfound");
    }
}
