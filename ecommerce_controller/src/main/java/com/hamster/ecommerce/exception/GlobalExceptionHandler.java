package com.hamster.ecommerce.exception;


import com.hamster.ecommerce.model.simple.ResponseMessage;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.UnexpectedTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mapping.MappingException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

/**
 * GlobalExceptionHandler.java - Intercepts all thrown Exceptions, decides the appropriate Http status, ErrorCode and
 * message, builds and return an ErrorResponse.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler
{
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Fallback handler which will handle all exceptions that don’t have specific exception handler.
     */
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request)
    {
        String errorMessage = ex.getMessage() != null ? ex.getMessage() : "Unknown error occurred in handleAllExceptions";
        log.error(String.format("handleAllExceptions - ErrorMessage: %s ", errorMessage), ex);
        log.error("Error in handleAllExceptions: {}", errorMessage, ex);

        ResponseMessage response = new ResponseMessage.ResponseMessageBuilder()
                .withMessage(errorMessage)
                .atDate(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handling invalid requests sent by client
     */

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request)
    {
        ResponseMessage response = new ResponseMessage.ResponseMessageBuilder()
                .withErrorCode(ErrorCode.BAD_REQUEST.getCode())
                .withMessage(ex.getLocalizedMessage())
                .atDate(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public final ResponseEntity<Object> methodArgumentMismatchExceptionHandler(MethodArgumentTypeMismatchException ex,
            WebRequest request)
    {
        log.error("methodArgumentMismatchExceptionHandler - ErrorMessage: {}, ParameterName: {}, Parameter: {} ",
                ex.getMessage(), ex.getParameter().getParameterName(), ex.getParameter().getParameter());

        String errorMessage = "Invalid type for parameter " + ex.getParameter().getParameterName() + ".";

        ResponseMessage response = new ResponseMessage.ResponseMessageBuilder()
                .withErrorCode(ErrorCode.BAD_REQUEST.getCode())
                .withMessage(errorMessage)
                .atDate(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request)
    {
        ResponseMessage response = new ResponseMessage.ResponseMessageBuilder()
                .withErrorCode(ErrorCode.INVALID_DATA.getCode())
                .withMessage(ex.getMessage())
                .atDate(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode httpStatusCode,
            WebRequest request)
    {

        StringBuilder errorMessages = new StringBuilder("Validation Failed for the following reasons: ");

        // Get all errors
        ex.getBindingResult().getFieldErrors().stream().forEach(error ->
                errorMessages.append(
                        String.format("Field: %s; Error: %s", error.getField(), error.getDefaultMessage()
                                .replace("<br>", ""))));

        ResponseMessage response = new ResponseMessage.ResponseMessageBuilder()
                .withErrorCode(ErrorCode.INVALID_DATA.getCode())
                .withMessage(errorMessages.toString())
                .atDate(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(UnexpectedTypeException.class)
    public final ResponseEntity<Object> handleUnexpectedTypeException(UnexpectedTypeException ex, WebRequest request)
    {

        ResponseMessage response = new ResponseMessage.ResponseMessageBuilder()
                .withErrorCode(ErrorCode.INVALID_DATA.getCode())
                .withMessage(ex.getMessage())
                .atDate(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationExceptions(TransactionSystemException ex, WebRequest request)
    {
        StringBuilder errorMessages = new StringBuilder("Constraint Violations detected: ");

        Throwable cause = ex.getRootCause();
        if (cause instanceof ConstraintViolationException)
        {
            Set<ConstraintViolation<?>> constraintViolations =
                    ((ConstraintViolationException) cause).getConstraintViolations();
            constraintViolations
                    .forEach(violationError -> errorMessages.append(
                            String.format("Field: %s  %s", violationError.getPropertyPath().toString(),
                                    violationError.getMessage())));
        }
        if (cause instanceof MappingException)
        {
            errorMessages.append(cause.getLocalizedMessage());
        }

        ResponseMessage response = new ResponseMessage.ResponseMessageBuilder()
                .withErrorCode(ErrorCode.INVALID_DATA.getCode())
                .withMessage(errorMessages.toString())
                .atDate(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public final ResponseEntity<Object> handleHibernateDataIntegrityViolationException(
            DataIntegrityViolationException ex, WebRequest request)
    {
        ResponseMessage response = new ResponseMessage.ResponseMessageBuilder()
                .withErrorCode(ErrorCode.INVALID_DATA.getCode())
                //.withMessage(ErrorCode.INVALID_DATA.getMessage())
                .withMessage(Objects.requireNonNull(ex.getRootCause()).toString())
                .atDate(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /*------------------------------+
    |   Handling custom Exceptions  |
    +------------------------------*/
    @ExceptionHandler(NotFoundException.class)
    public final ResponseEntity<Object> handleNotFoundException(NotFoundException ex, WebRequest request)
    {
        ResponseMessage response = new ResponseMessage.ResponseMessageBuilder()
                .withErrorCode(ErrorCode.MISSING_DATA.getCode())
                .withMessage(ex.getMessage())
                .atDate(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    public final ResponseEntity<Object> handleConflictException(ConflictException ex, WebRequest request)
    {
        String errorMessage = ex.getMessage() != null ? ex.getMessage() : (ex.getErrorCode() != null ?
                ex.getErrorCode().getMessage() : null);

        ResponseMessage response = new ResponseMessage.ResponseMessageBuilder()
                .withErrorCode(ex.getErrorCode() != null ? ex.getErrorCode().getCode() :
                        ErrorCode.CONFLICT.getCode())
                .withMessage(errorMessage)
                .atDate(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidDataException.class)
    public final ResponseEntity<Object> handleInvalidDataException(InvalidDataException ex, WebRequest request)
    {
        StringBuilder errorMessages = new StringBuilder("Validation Failed for the following reasons: ");
        ex.getInvalidDataExceptionList().forEach(error ->
                errorMessages.append(String.format("Object: %s; Field: %s; Error: %s",
                        error.getObject(), error.getField(), error.getErrorMessage())));

        ResponseMessage response = new ResponseMessage.ResponseMessageBuilder()
                .withErrorCode(ErrorCode.INVALID_DATA.getCode())
                .withMessage(errorMessages.toString())
                .atDate(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
