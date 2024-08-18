package com.codeheadsystems.smr.impl;

import com.codeheadsystems.smr.Event;
import com.codeheadsystems.smr.ImmutableState;
import com.codeheadsystems.smr.State;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class StateMachineDefinitionBuilder<T> {

  final Set<State> states;
  final Map<State, Map<Event, State>> transitions;
  State initialState;
  boolean useExceptions;

  public StateMachineDefinitionBuilder() {
    this.states = new HashSet<>();
    this.transitions = new HashMap<>();
  }

  public StateMachineDefinitionBuilder<T> withExceptions(boolean useExceptions) {
    this.useExceptions = useExceptions;
    return this;
  }

  public StateMachineDefinitionBuilder<T> addState(final String name) {
    return addState(ImmutableState.of(name));
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
