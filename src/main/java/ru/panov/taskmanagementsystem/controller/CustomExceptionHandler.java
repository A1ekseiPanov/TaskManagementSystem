package ru.panov.taskmanagementsystem.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.panov.taskmanagementsystem.exception.DuplicateException;
import ru.panov.taskmanagementsystem.exception.InputDataConflictException;
import ru.panov.taskmanagementsystem.exception.NotFoundException;


@RestControllerAdvice
public class CustomExceptionHandler {

       @ExceptionHandler(value
            = {NotFoundException.class})
    protected ResponseEntity<ProblemDetail> notFoundError(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(value
            = {InputDataConflictException.class})
    protected ResponseEntity<ProblemDetail> InputDataError(InputDataConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    protected ResponseEntity<ProblemDetail> IllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage()));
    }

    @ExceptionHandler(value
            = {DuplicateException.class})
    protected ResponseEntity<ProblemDetail> duplicationError(DuplicateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(value
            = {AccessDeniedException.class})
    protected ResponseEntity<ProblemDetail> accessDeniedError(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage()));
    }


    @ExceptionHandler(value = {BindException.class})
    public ResponseEntity<ProblemDetail> handleBindException(BindException ex) {
        ProblemDetail problemDetail = ProblemDetail
                .forStatusAndDetail(HttpStatus.BAD_REQUEST, "ошибка валидации");
        problemDetail.setProperty("errors",
                ex.getAllErrors().stream()
                        .map(ObjectError::getDefaultMessage)
                        .toList());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(problemDetail);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ProblemDetail> handleExpiredJwtException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()));
    }
}