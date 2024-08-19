package com.codeheadsystems.smr;

import org.immutables.value.Value;

/**
 * When a transition is occurring, callback consumers are given this object with the phase of
 * the transition for the state in question.
 */
@Value.Immutable
public interface Callback {

  Phase phase();

  State state();

  Context context();

}
