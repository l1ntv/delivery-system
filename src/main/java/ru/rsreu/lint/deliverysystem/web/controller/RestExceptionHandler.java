package ru.rsreu.lint.deliverysystem.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.rsreu.lint.deliverysystem.model.exception.ExceptionBody;
import ru.rsreu.lint.deliverysystem.model.exception.OrderNotFoundException;
import ru.rsreu.lint.deliverysystem.model.exception.UserNotFoundException;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionBody userNotFound(UserNotFoundException e) {
        return new ExceptionBody("User not found.");
    }

    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionBody orderNotFound(OrderNotFoundException e) {
        return new ExceptionBody("Order not found.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody postNotValid(MethodArgumentNotValidException e) {
        StringBuilder builder = new StringBuilder();
        for (ObjectError error : e.getAllErrors()) {
            builder.append(error.getDefaultMessage())
                    .append(" ");
        }
        return new ExceptionBody(
                builder.toString()
        );
    }
}
