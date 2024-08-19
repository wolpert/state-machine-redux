package com.codeheadsystems.smr;

import org.immutables.value.Value;
import org.immutables.value.Value.Immutable;

/**
 * An event causes the state machine (context) to be transitions from one state to another.
 */
@Immutable
public interface Event {

  @Value.Parameter
  String name();

}
