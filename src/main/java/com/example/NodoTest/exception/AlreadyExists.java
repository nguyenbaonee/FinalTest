package com.example.NodoTest.exception;

public class AlreadyExists extends RuntimeException {
    public AlreadyExists() {
        super("response.exists");
    }
}
