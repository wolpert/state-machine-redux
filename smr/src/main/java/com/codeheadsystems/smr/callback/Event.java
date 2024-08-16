package com.codeheadsystems.smr.callback;

/**
 * The state machine events. When the event is dispatched, the
 * following callbacks are called in order: ENTER, EXIT.
 */
public enum Event {
  ENTER,
  TICK,
  EXIT
}
