package com.jomaleda.ravenpack.interview.aspect;

import com.jomaleda.ravenpack.interview.annotation.SimulateLatency;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LatencyAspectTest {

    private LatencyAspect latencyAspect;
    
    @Mock
    private ProceedingJoinPoint joinPoint;
    
    @Mock
    private SimulateLatency simulateLatency;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        latencyAspect = new LatencyAspect();
    }

    @Test
    void simulateLatency_NormalExecution_AddsLatencyAndProceedsWithMethod() throws Throwable {
        String expectedResult = "test result";
        when(joinPoint.proceed()).thenReturn(expectedResult);
        
        long startTime = System.currentTimeMillis();
        Object result = latencyAspect.simulateLatency(joinPoint, simulateLatency);
        long duration = System.currentTimeMillis() - startTime;
        
        assertEquals(expectedResult, result);
        assertTrue(duration >= 50, "Should have at least 50ms latency");
        assertTrue(duration <= 300, "Should not exceed reasonable latency bounds");
        verify(joinPoint).proceed();
    }

    @Test
    void simulateLatency_JoinPointThrowsException_PropagatesException() throws Throwable {
        RuntimeException expectedException = new RuntimeException("Test exception");
        when(joinPoint.proceed()).thenThrow(expectedException);
        
        RuntimeException thrownException = assertThrows(RuntimeException.class, () ->
            latencyAspect.simulateLatency(joinPoint, simulateLatency));
        
        assertEquals(expectedException, thrownException);
        verify(joinPoint).proceed();
    }

    @Test
    void simulateLatency_InterruptedThread_ThrowsRuntimeException() throws Throwable {
        Thread testThread = new Thread(() -> {
            Thread.currentThread().interrupt();
            assertThrows(RuntimeException.class, () ->
                latencyAspect.simulateLatency(joinPoint, simulateLatency));
        });
        
        testThread.start();
        testThread.join();
        assertTrue(testThread.isInterrupted() || !testThread.isAlive());
    }

    @Test
    void simulateLatency_MultipleInvocations_EachHasLatency() throws Throwable {
        when(joinPoint.proceed()).thenReturn("result");
        
        for (int i = 0; i < 3; i++) {
            long startTime = System.currentTimeMillis();
            latencyAspect.simulateLatency(joinPoint, simulateLatency);
            long duration = System.currentTimeMillis() - startTime;
            
            assertTrue(duration >= 50, "Each call should have latency");
        }
        
        verify(joinPoint, times(3)).proceed();
    }
}