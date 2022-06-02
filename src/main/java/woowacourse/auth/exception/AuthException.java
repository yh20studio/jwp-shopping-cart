package woowacourse.auth.exception;

import org.springframework.http.HttpStatus;

public abstract class AuthException extends RuntimeException {

    private final HttpStatus httpStatus;

    public AuthException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public final HttpStatus getHttpStatus() {
        return httpStatus;
    }
}