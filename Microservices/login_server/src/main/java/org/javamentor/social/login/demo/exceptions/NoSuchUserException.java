package org.javamentor.social.login.demo.exceptions;

public class NoSuchUserException extends RuntimeException{
    public NoSuchUserException(String message) { super(message); }

    public NoSuchUserException(String message, Throwable cause){ super(message, cause);}
}
