package com.codeheadsystems.smr;

/**
 * Provides for an unchecked exception class useful for the state machine.
 */
public class StateMachineException extends RuntimeException {
  public StateMachineException(String message) {
    super(message);
  }
}
