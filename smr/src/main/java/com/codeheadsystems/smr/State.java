package com.codeheadsystems.smr;

import org.immutables.value.Value;

/**
 * A state of the state machine (context).
 */
@Value.Immutable
public interface State {

  @Value.Parameter
  String name();

}
