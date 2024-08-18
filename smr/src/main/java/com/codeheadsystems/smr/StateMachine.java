package com.codeheadsystems.smr;

import com.codeheadsystems.smr.callback.Callback;
import com.codeheadsystems.smr.callback.Phase;
import com.codeheadsystems.smr.impl.StateMachineImpl;
import java.util.Set;
import java.util.function.Consumer;

/**
 * State machines are immutable once built regarding the states and available
 * transitions.
 */
public interface StateMachine extends Context {

  static StateMachineImpl.Builder builder() {
    return new StateMachineImpl.Builder();
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
   * Get the events that are valid for the current state.
   *
   * @return set of events
   */
  Set<Event> events();

  /**
   * Get the events that are valid for the given state.
   *
   * @param state to check.
   * @return set of event.
   */
  Set<Event> events(State state);

  void enable(State state, Phase phase, Consumer<Callback> contextConsumer);

  void disable(State state, Phase phase, Consumer<Callback> contextConsumer);

  /**
   * Dispatch an event to the state machine.
   *
   * @param event to dispatch.
   * @return the new state if changed. Else the current state.
   */
  State dispatch(Event event);

  /**
   * Tick the state machine. Basically causes the callbacks on the current
   * state to execute.
   */
  void tick();

}
