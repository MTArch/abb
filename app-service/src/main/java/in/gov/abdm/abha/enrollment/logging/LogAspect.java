package in.gov.abdm.abha.enrollment.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Objects;

@Aspect
@Component
@Slf4j
public class LogAspect {


    public static final String EXITING_WITH_ARGUMENT = "Exiting: {}.{}() with argument[s] = {}";

    /**
     * Pointcut that matches all Spring beans in the application's main packages.
     */
    @Pointcut("within(in.gov.abdm.abha.enrollment.controller..*)")
    public void logPointcut() {
    }

    /**
     * Advice that logs when a method is entered and exited.
     *
     * @param joinPoint join point for advice
     * @return result
     * @throws Throwable throws IllegalArgumentException
     */
    @Around("logPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Entering: {}.{}() with argument[s] = {}", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));

        try {
            Object result = joinPoint.proceed();
            if (result instanceof Mono) {
                var monoResult = (Mono<?>) result;

                return monoResult.doOnSuccess(o -> {
                    var response = "";
                    if (Objects.nonNull(o)) {
                        response = o.toString();
                    }
                    log.info(EXITING_WITH_ARGUMENT,
                            joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(),
                            response);
                });
            }if (result instanceof Flux) {
                var fluxResult = (Flux<?>) result;
                return fluxResult.map(fluxItem -> {
                    log.info(EXITING_WITH_ARGUMENT, joinPoint.getSignature().getDeclaringTypeName(),
                            joinPoint.getSignature().getName(), fluxItem);
                    return fluxItem;
                });

            } else {
                log.info(EXITING_WITH_ARGUMENT, joinPoint.getSignature().getDeclaringTypeName(),
                        joinPoint.getSignature().getName(), result);
            }
            return  result;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument: {} in {}.{}()", Arrays.toString(joinPoint.getArgs()),
                    joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
            throw e;
        }
    }
}
