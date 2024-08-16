package com.codeheadsystems.smr;

import java.util.concurrent.atomic.AtomicReference;

/**
 * You can have many contexts for a single state machine. And the
 * state machine manages this one context.
 */
@FunctionalInterface
public interface Context {

  AtomicReference<State> reference();

  /**
   * You can extend this to generate your own context easily enough.
   */
  abstract class Impl implements Context {

    private final AtomicReference<State> state;

    public Impl(StateMachine stateMachine) {
      this(stateMachine.state());
    }

    public Impl(State initialState) {
      this.state = new AtomicReference<>(initialState);
    }

    @Override
    public AtomicReference<State> reference() {
      return state;
    }
  }

}
