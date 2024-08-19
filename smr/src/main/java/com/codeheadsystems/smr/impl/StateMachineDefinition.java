package com.codeheadsystems.smr.impl;

import com.codeheadsystems.smr.Event;
import com.codeheadsystems.smr.State;
import com.codeheadsystems.smr.StateMachineException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A state machine definition is a set of states and transitions between them.
 * It is immutable once built.
 */
public class StateMachineDefinition {

  private static final Logger log = LoggerFactory.getLogger(StateMachineDefinition.class);

  private final Map<State, Map<Event, State>> transitions;
  private final State initialState;

  StateMachineDefinition(final StateMachineDefinitionBuilder<?> builder) {
    log.info("StateMachineDefinition()");
    if (builder.initialState == null) {
      throw new StateMachineException("Initial state is required.");
    }
    this.transitions = builder.transitions;
    this.initialState = builder.initialState;
  }

  public static StateMachineDefinition.Builder builder() {
    return new StateMachineDefinition.Builder();
  }

  /**
   * New state machines start with this initial state.
   * @return the initial state.
   */
  public State initialState() {
    return initialState;
  }

  /**
   * List of all states within the state machine.
   * @return set of states.
   */
  public Set<State> states() {
    return transitions.keySet();
  }

  /**
   * List of all events that can be triggered from the given state.
   * @param state that owns the events.
   * @return set of events.
   */
  public Set<Event> events(final State state) {
    return transitions.get(state).keySet();
  }

  /**
   * Check if the state machine has the given state.
   * @param state to check.
   * @return true if the state is in the state machine.
   */
  public boolean hasState(final State state) {
    return transitions.containsKey(state);
  }

  /**
   * Gets the next state for the given state and event. May not exist so return an optional.
   * @param state to check.
   * @param event to check.
   * @return the optional that contains the next state, if any.
   */
  public Optional<State> forEvent(final State state,
                                  final Event event) {
    if (hasState(state)) {
      return Optional.ofNullable(transitions.get(state).get(event));
    } else {
      return Optional.empty();
    }
  }

  public static class Builder extends StateMachineDefinitionBuilder<StateMachineDefinition> {

    @Override
    public StateMachineDefinition build() {
      return new StateMachineDefinition(this);
    }

  }

}
