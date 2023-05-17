package com.example.board.module.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(Exception400.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ProblemDetail onException400(Exception400 exception400) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()),
                exception400.getMessage()
        );
    }

    @ExceptionHandler(Exception401.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ProblemDetail onException400(Exception401 exception401) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatusCode.valueOf(HttpStatus.UNAUTHORIZED.value()),
                exception401.getMessage()
        );
    }

    @ExceptionHandler(Exception403.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ProblemDetail onException400(Exception403 exception403) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatusCode.valueOf(HttpStatus.FORBIDDEN.value()),
                exception403.getMessage()
        );
    }

}
