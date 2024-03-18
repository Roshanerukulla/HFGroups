package com.hf.groups.exception;

public class GroupAlreadyExistException extends RuntimeException {

    public GroupAlreadyExistException(String message) {
        super(message);
    }
}
