package com.eternus.ratelimit.circuitbreaker;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestCircuitBreaker {

    @Test
    public void basicUsage() throws Exception {
        CircuitBreakerConfiguration config = new CircuitBreakerConfiguration();
        config.setThreshold(1);
        config.setTimeoutInMillis(100);
        CircuitBreaker circuitBreaker = config.createCircuitBreaker();

        assertEquals(0L, circuitBreaker.getTripCount());
        assertEquals("CLOSED", circuitBreaker.getCurrentState());

        Runnable failingOperation = new Runnable() {

            /**
             * {@inheritDoc}
             */
            public void run() {
                doFailingOp();
            }
        };

        tryGuardedOperation(circuitBreaker, failingOperation);

        assertEquals(0L, circuitBreaker.getTripCount());
        assertEquals("CLOSED", circuitBreaker.getCurrentState());

        tryGuardedOperation(circuitBreaker, failingOperation);

        assertEquals("circuit breaker has been tripped after second failure", 1L, circuitBreaker.getTripCount());
        assertEquals("OPEN", circuitBreaker.getCurrentState());
    }

    @Test
    public void willAttemptReset() throws Exception {
        CircuitBreakerConfiguration config = new CircuitBreakerConfiguration();
        config.setThreshold(1);
        config.setTimeoutInMillis(100);
        CircuitBreaker circuitBreaker = config.createCircuitBreaker();

        assertEquals(0L, circuitBreaker.getTripCount());
        assertEquals("CLOSED", circuitBreaker.getCurrentState());

        Runnable failingOperation = new Runnable() {

            /**
             * {@inheritDoc}
             */
            public void run() {
                doFailingOp();
            }
        };

        tryGuardedOperation(circuitBreaker, failingOperation);

        assertEquals(0L, circuitBreaker.getTripCount());
        assertEquals("CLOSED", circuitBreaker.getCurrentState());

        tryGuardedOperation(circuitBreaker, failingOperation);

        assertEquals("circuit breaker has been tripped after second failure", 1L, circuitBreaker.getTripCount());
        assertEquals("OPEN", circuitBreaker.getCurrentState());

        Thread.sleep(100);

        tryGuardedOperation(circuitBreaker, new Runnable() {

            /**
             * {@inheritDoc}
             */
            public void run() {
                // no-op - simulate successful operation
            }
        });

        assertEquals("Operation was successful and the CircuitBreaker is now closed again", "CLOSED", circuitBreaker
                .getCurrentState());
    }

    @Test
    public void canBeResetAndSuccessfullyCarryOn() throws Exception {
        CircuitBreakerConfiguration config = new CircuitBreakerConfiguration();
        config.setThreshold(0);
        config.setTimeoutInMillis(100);
        CircuitBreaker circuitBreaker = config.createCircuitBreaker();

        assertEquals(0L, circuitBreaker.getTripCount());
        assertEquals("CLOSED", circuitBreaker.getCurrentState());

        Runnable failingOperation = new Runnable() {

            /**
             * {@inheritDoc}
             */
            public void run() {
                doFailingOp();
            }
        };

        tryGuardedOperation(circuitBreaker, failingOperation);

        assertEquals("circuit breaker has been tripped after first failure", 1L, circuitBreaker.getTripCount());
        assertEquals("OPEN", circuitBreaker.getCurrentState());

        circuitBreaker.reset();

        assertEquals("CLOSED", circuitBreaker.getCurrentState());
        
        tryGuardedOperation(circuitBreaker, new Runnable() {

            /**
             * {@inheritDoc}
             */
            public void run() {
                // no-op - simulate successful operation
            }
        });

        assertEquals("Operation was successful and the CircuitBreaker is now closed again", "CLOSED", circuitBreaker
                .getCurrentState());
         
    }
    
    private void tryGuardedOperation(CircuitBreaker circuitBreaker, Runnable operation) {
        try {
            circuitBreaker.before();
            operation.run();
            circuitBreaker.after();
        } catch (Throwable e) {
            circuitBreaker.handleFailure();
        }
    }

    private void doFailingOp() {
        throw new RuntimeException("Simulate failing operation");
    }
}
