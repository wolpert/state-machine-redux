package com.codeheadsystems.smr;

/**
 * You can have many contexts for a single state machine. And the
 * state machine manages this one context.
 */
public interface Context {

  State state();

  State setState(State state);

  /**
   * You can extend this to generate your own context easily enough.
   */
  abstract class Impl implements Context {
    private State state;

    @Override
    public State state() {
      return state;
    }

    @Override
    public State setState(State state) {
      this.state = state;
      return state;
    }
  }

}
