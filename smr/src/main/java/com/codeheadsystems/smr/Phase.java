package com.codeheadsystems.smr;

/**
 * The state machine transition phases. When the event is dispatched, the
 * following callbacks are called in order: EXIT, ENTER.
 */
public enum Phase {
  ENTER,
  TICK,
  EXIT
}
