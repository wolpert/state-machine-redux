package com.codeheadsystems.smr;

import com.codeheadsystems.smr.impl.StateMachineBuilder;

public interface StateMachine {

  static StateMachineBuilder builder() {
    return new StateMachineBuilder();
  }

  /**
   * The current state of the state machine.
   *
   * @return the current state.
   */
  State state();

  /**
   * Dispatch an action to the state machine.
   *
   * @param action to dispatch.
   * @return the new state if changed. Else the current state.
   */
  State dispatch(Action action);

}
