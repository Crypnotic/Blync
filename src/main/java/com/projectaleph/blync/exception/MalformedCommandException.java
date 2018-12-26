package com.projectaleph.blync.exception;

public class MalformedCommandException extends Exception {

    private static final long serialVersionUID = -2200840558260067809L;

    public MalformedCommandException() {
    }

    public MalformedCommandException(String string) {
        super(string);
    }
}
