package com.codeheadsystems.smr;

import org.immutables.value.Value;

/**
 * A state of the state machine (context).
 */
@Value.Immutable
public interface State {

  static State of(String name) {
    return ImmutableState.of(name);
  }

  @Value.Parameter
  String name();

}
