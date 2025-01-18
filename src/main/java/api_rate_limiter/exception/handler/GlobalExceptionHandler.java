package api_rate_limiter.exception.handler;

import api_rate_limiter.exception.api_error.ApiError;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleConstraintViolationException(ConstraintViolationException e) {
        return new ApiError(HttpStatus.BAD_REQUEST.value(), e.getMessage().split(":")[1]);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleHttpRequestMethodNotSupportedException() {
        return new ApiError(HttpStatus.BAD_REQUEST.value(), "The application accepts only HTTP GET request whose url matches with /api/v1/**");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingRequestHeaderException(MissingRequestHeaderException e) {
        return new ApiError(HttpStatus.BAD_REQUEST.value(), "HTTP header " + e.getHeaderName() + " is required");
    }

    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleGeneralException() {
        return new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some thing happened on our side");
    }

}
