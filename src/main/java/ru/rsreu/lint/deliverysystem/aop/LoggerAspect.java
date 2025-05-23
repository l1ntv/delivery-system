package ru.rsreu.lint.deliverysystem.aop;

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

    @Pointcut("@annotation(ru.rsreu.lint.deliverysystem.aop.Loggable)")
    public void loggableMethod() {

    }

    @Before("loggableMethod()")
    public void logBefore(JoinPoint joinPoint) {
        logger.info("Вызывается метод: {}. Параметры: {}",
                joinPoint.getSignature().toShortString(),
                Arrays.asList(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "loggableMethod()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        logger.info("Метод завершён успешно: {}. Результат: {}",
                joinPoint.getSignature().toShortString(),
                result != null ? result.toString() : "void");
    }

    @AfterThrowing(pointcut = "loggableMethod()", throwing = "error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        logger.error("Ошибка в методе: {}. Тип исключения: {}",
                joinPoint.getSignature().toShortString(),
                error.toString());
    }
}
