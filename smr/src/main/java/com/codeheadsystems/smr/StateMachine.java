package com.codeheadsystems.smr;

import com.codeheadsystems.smr.callback.Callback;
import com.codeheadsystems.smr.callback.Event;
import com.codeheadsystems.smr.impl.StateMachineBuilder;
import java.util.Set;
import java.util.function.Consumer;

/**
 * State machines are immutable once built regarding the states and available
 * transitions.
 */
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
   * Get the states that are valid for the current state machine.
   *
   * @return set of states.
   */
  Set<State> states();

  /**
   * Get the actions that are valid for the current state.
   *
   * @return set of actions
   */
  Set<Action> actions();

  /**
   * Get the actions that are valid for the given state.
   *
   * @param state to check.
   * @return set of actions.
   */
  Set<Action> actions(State state);

  void enable(State state, Event event, Consumer<Callback> contextConsumer);

  void disable(State state, Event event, Consumer<Callback> contextConsumer);

  /**
   * Dispatch an action to the state machine.
   *
   * @param action to dispatch.
   * @return the new state if changed. Else the current state.
   */
  State dispatch(Action action);

  /**
   * Tick the state machine. Basically causes the callbacks on the current
   * state to execute.
   */
  void tick();

}
