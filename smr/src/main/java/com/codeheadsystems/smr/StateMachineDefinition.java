package com.codeheadsystems.smr;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.annotation.concurrent.NotThreadSafe;
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

  public StateMachineDefinition(final StateMachineDefinitionBuilder<?> builder) {
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
   *
   * @return the initial state.
   */
  public State initialState() {
    return initialState;
  }

  /**
   * All transitions within the state machine.
   *
   * @return map of state to map of event to state.
   */
  public Map<State, Map<Event, State>> getTransitions() {
    return transitions;
  }

  /**
   * List of all states within the state machine.
   *
   * @return set of states.
   */
  public Set<State> states() {
    return transitions.keySet();
  }

  /**
   * List of all events that can be triggered from the given state.
   *
   * @param state that owns the events.
   * @return set of events.
   */
  public Set<Event> events(final State state) {
    return transitions.get(state).keySet();
  }

  /**
   * Check if the state machine has the given state.
   *
   * @param state to check.
   * @return true if the state is in the state machine.
   */
  public boolean hasState(final State state) {
    return transitions.containsKey(state);
  }

  /**
   * Gets the next state for the given state and event. May not exist so return an optional.
   *
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

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final StateMachineDefinition that = (StateMachineDefinition) o;
    return Objects.equals(transitions, that.transitions) && Objects.equals(initialState, that.initialState);
  }

  @Override
  public int hashCode() {
    return Objects.hash(transitions, initialState);
  }

  public static class Builder extends StateMachineDefinitionBuilder<StateMachineDefinition> {

    @Override
    public StateMachineDefinition build() {
      return new StateMachineDefinition(this);
    }

  }

  @NotThreadSafe
  public abstract static class StateMachineDefinitionBuilder<T> {

    protected final Set<State> states;
    protected final Map<State, Map<Event, State>> transitions;
    State initialState;

    public StateMachineDefinitionBuilder() {
      this.states = new HashSet<>();
      this.transitions = new HashMap<>();
    }

    public StateMachineDefinitionBuilder<T> addState(final String name) {
      return addState(State.of(name));
    }

    public StateMachineDefinitionBuilder<T> addState(final State state) {
      states.add(state);
      transitions.put(state, new HashMap<>());
      return this;
    }

    public StateMachineDefinitionBuilder<T> addTransition(final State from, final Event event, final State to) {
      if (!states.contains(from)) {
        throw new IllegalArgumentException("State " + from + " is not in the state machine.");
      }
      if (!states.contains(to)) {
        throw new IllegalArgumentException("State " + to + " is not in the state machine.");
      }
      transitions.get(from).put(event, to);
      return this;
    }

    public StateMachineDefinitionBuilder<T> setInitialState(final State initialState) {
      if (!states.contains(initialState)) {
        throw new IllegalArgumentException("State " + initialState + " is not in the state machine.");
      }
      this.initialState = initialState;
      return this;
    }

    public abstract T build();

  }
}
