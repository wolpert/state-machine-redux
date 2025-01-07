package com.codeheadsystems.smr;

import org.immutables.value.Value;
import org.immutables.value.Value.Immutable;

/**
 * An event causes the state machine (context) to be transitions from one state to another.
 */
@Immutable
public interface Event {

  static Event of(String name) {
    return ImmutableEvent.of(name);
  }

  @Value.Parameter
  String name();

}
