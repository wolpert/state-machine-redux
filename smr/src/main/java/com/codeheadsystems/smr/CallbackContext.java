package com.codeheadsystems.smr;

import org.immutables.value.Value;

@Value.Immutable
public interface CallbackContext {

  StateMachine stateMachine();

  CallbackContext.Event event();

  State state();

  /**
   * The state machine events. When the event is dispatched, the
   * following callbacks are called in order: ENTER, EXIT.
   */
  enum Event {
    ENTER,
    TICK,
    EXIT
  }
}
