package com.pc.greenbay.controller;

import com.pc.greenbay.exception.*;
import com.pc.greenbay.model.ErrorDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(RecordNotFoundException.class)
    protected ResponseEntity<Object> handleRecordNotFoundException(RecordNotFoundException ex, WebRequest request) {
        log.error("Record not found", ex);
        return handleExceptionInternal(ex,
                new ErrorDTO(ex.getMessage()),
                new HttpHeaders(), HttpStatus.NOT_FOUND,
                request);
    }

    @ExceptionHandler(NoMoneyException.class)
    protected ResponseEntity<Object> handleNoMoneyException(NoMoneyException ex, WebRequest request) {
        log.error("Bidder has no money", ex);
        return handleExceptionInternal(ex,
                new ErrorDTO(ex.getMessage()),
                new HttpHeaders(), HttpStatus.BAD_REQUEST,
                request);
    }

    @ExceptionHandler(NotEnoughMoneyException.class)
    protected ResponseEntity<Object> handleNotEnoughMoneyException(NotEnoughMoneyException ex, WebRequest request) {
        log.error("Bidder has not enough money", ex);
        return handleExceptionInternal(ex,
                new ErrorDTO(ex.getMessage()),
                new HttpHeaders(), HttpStatus.BAD_REQUEST,
                request);
    }

    @ExceptionHandler(BidOnOwnItemException.class)
    protected ResponseEntity<Object> handleBidOnOwnItemException(BidOnOwnItemException ex, WebRequest request) {
        log.error("Cannot bid on own item", ex);
        return handleExceptionInternal(ex,
                new ErrorDTO(ex.getMessage()),
                new HttpHeaders(), HttpStatus.BAD_REQUEST,
                request);
    }

    @ExceptionHandler(ItemNotSellableException.class)
    protected ResponseEntity<Object> handleItemNotSellableException(ItemNotSellableException ex, WebRequest request) {
        log.error("Item not sellable", ex);
        return handleExceptionInternal(ex,
                new ErrorDTO(ex.getMessage()),
                new HttpHeaders(), HttpStatus.BAD_REQUEST,
                request);
    }

    @ExceptionHandler(LowBidException.class)
    protected ResponseEntity<Object> handleLowBidException(LowBidException ex, WebRequest request) {
        log.error("Bid too low", ex);
        return handleExceptionInternal(ex,
                new ErrorDTO(ex.getMessage()),
                new HttpHeaders(), HttpStatus.BAD_REQUEST,
                request);
    }

    @ExceptionHandler(DataAccessException.class)
    protected ResponseEntity<Object> handleDataAccessException(
            DataAccessException ex,
            WebRequest request) {
        log.error("Database access error: ", ex);
        return handleExceptionInternal(ex,
                new ErrorDTO(ex.getMessage()),
                new HttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE,
                request);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    protected ResponseEntity<Object> handleUsernameNotFoundException(UsernameNotFoundException ex, WebRequest request) {
        log.error("Cannot bid on own item", ex);
        return handleExceptionInternal(ex,
                new ErrorDTO(ex.getMessage()),
                new HttpHeaders(), HttpStatus.BAD_REQUEST,
                request);
    }

    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<Object> handleBadCredentialsException(
            BadCredentialsException ex,
            WebRequest request) {
        log.error("Authentication failed: ", ex);
        return handleExceptionInternal(ex,
                new ErrorDTO("Authentication failed." +
                        " Incorrect username and/or password."),
                new HttpHeaders(), HttpStatus.UNAUTHORIZED,
                request);
    }
}
