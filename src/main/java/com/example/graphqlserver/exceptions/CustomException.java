package com.example.graphqlserver.exceptions;

public class CustomException extends RuntimeException {
    public CustomException(ErrorCode message) {
        super(message.getCode());
    }
}
