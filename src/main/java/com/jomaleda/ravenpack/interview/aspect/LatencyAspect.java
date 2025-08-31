package com.jomaleda.ravenpack.interview.aspect;

import com.jomaleda.ravenpack.interview.annotation.SimulateLatency;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

/**
 * Aspect for simulating network latency on annotated methods.
 */
@Aspect
@Component
public class LatencyAspect {
    private final SecureRandom secureRandom = new SecureRandom();

    @Around("@annotation(simulateLatency)")
    public Object simulateLatency(ProceedingJoinPoint joinPoint, SimulateLatency simulateLatency) throws Throwable {
        try {
            long latency = 50 + secureRandom.nextInt(151);
            TimeUnit.MILLISECONDS.sleep(latency);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Service call was interrupted", e);
        }
        return joinPoint.proceed();
    }
}