package ru.rsreu.lint.deliverysystem.service;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggerAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggerAspect.class);

    @Pointcut("execution(* ru.rsreu.lint.deliverysystem.service.ClientService.*(..)) || " +
            "execution(* ru.rsreu.lint.deliverysystem.service.CourierService.*(..)) || " +
            "execution(* ru.rsreu.lint.deliverysystem.service.OrderService.*(..))")
    public void serviceMethods() {
    }

    @Before("serviceMethods()")
    public void logBefore(JoinPoint joinPoint) {
        StringBuilder sb = new StringBuilder();
        sb.append("Вызывается метод: ").append(joinPoint.getSignature().toShortString()).append(". ");
        if (joinPoint.getArgs().length > 0) {
            sb.append("Параметры: ").append(Arrays.asList(joinPoint.getArgs()));
        }
        logger.info(sb.toString());
    }

    @AfterReturning(pointcut = "serviceMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        StringBuilder sb = new StringBuilder();
        sb.append("Метод завершён успешно: ").append(joinPoint.getSignature().toShortString()).append(". ");
        if (result != null) {
            sb.append("Результат: ").append(result);
        }
        logger.info(sb.toString());
    }

    @AfterThrowing(pointcut = "serviceMethods()", throwing = "error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        logger.error("Ошибка в методе: {}", joinPoint.getSignature().toShortString(), error);
    }
}