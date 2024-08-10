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

/**
 * Глобальный обработчик исключений.
 */
@RestControllerAdvice
public class CustomExceptionHandler {

    /**
     * Обрабатывает исключение NotFoundException.
     *
     * @param ex исключение NotFoundException
     * @return ответ с деталями ошибки и статусом NOT_FOUND
     */
    @ExceptionHandler(value
            = {NotFoundException.class})
    protected ResponseEntity<ProblemDetail> notFoundError(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    /**
     * Обрабатывает исключение InputDataConflictException.
     *
     * @param ex исключение InputDataConflictException
     * @return ответ с деталями ошибки и статусом CONFLICT
     */
    @ExceptionHandler(value
            = {InputDataConflictException.class})
    protected ResponseEntity<ProblemDetail> inputDataError(InputDataConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage()));
    }

    /**
     * Обрабатывает исключение IllegalArgumentException.
     *
     * @param ex исключение IllegalArgumentException
     * @return ответ с деталями ошибки и статусом FORBIDDEN
     */
    @ExceptionHandler(value = {IllegalArgumentException.class})
    protected ResponseEntity<ProblemDetail> illegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage()));
    }

    /**
     * Обрабатывает исключение DuplicateException.
     *
     * @param ex исключение DuplicateException
     * @return ответ с деталями ошибки и статусом BAD_REQUEST
     */
    @ExceptionHandler(value
            = {DuplicateException.class})
    protected ResponseEntity<ProblemDetail> duplicationError(DuplicateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    /**
     * Обрабатывает исключение AccessDeniedException.
     *
     * @param ex исключение AccessDeniedException
     * @return ответ с деталями ошибки и статусом UNAUTHORIZED
     */
    @ExceptionHandler(value
            = {AccessDeniedException.class})
    protected ResponseEntity<ProblemDetail> accessDeniedError(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage()));
    }

    /**
     * Обрабатывает исключение BindException.
     *
     * @param ex исключение BindException
     * @return ответ с деталями ошибки и статусом BAD_REQUEST
     */
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

    /**
     * Обрабатывает общее исключение.
     *
     * @param ex общее исключение
     * @return ответ с деталями ошибки и статусом INTERNAL_SERVER_ERROR
     */
    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ProblemDetail> handleExpiredJwtException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()));
    }
}