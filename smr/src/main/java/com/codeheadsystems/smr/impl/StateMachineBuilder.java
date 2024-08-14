package com.codeheadsystems.smr.impl;

import com.codeheadsystems.smr.Action;
import com.codeheadsystems.smr.ImmutableState;
import com.codeheadsystems.smr.State;
import com.codeheadsystems.smr.StateMachine;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class StateMachineBuilder {

  final Set<State> states;
  final Map<State, Map<Action, State>> transitions;
  State initialState;
  boolean useExceptions;

  public StateMachineBuilder() {
    this.states = new HashSet<>();
    this.transitions = new HashMap<>();
  }

  public StateMachineBuilder withExceptions(boolean useExceptions) {
    this.useExceptions = useExceptions;
    return this;
  }

  public StateMachineBuilder addState(final String name) {
    return addState(ImmutableState.of(name));
  }

  public StateMachineBuilder addState(final State state) {
    states.add(state);
    transitions.put(state, new HashMap<>());
    return this;
  }

  public StateMachineBuilder addTransition(final State from, final Action action, final State to) {
    if (!states.contains(from)) {
      throw new IllegalArgumentException("State " + from + " is not in the state machine.");
    }
    if (!states.contains(to)) {
      throw new IllegalArgumentException("State " + to + " is not in the state machine.");
    }
    transitions.get(from).put(action, to);
    return this;
  }

  public StateMachineBuilder setInitialState(final State initialState) {
    if (!states.contains(initialState)) {
      throw new IllegalArgumentException("State " + initialState + " is not in the state machine.");
    }
    this.initialState = initialState;
    return this;
  }

  public StateMachine build() {
    if (initialState == null) {
      throw new IllegalStateException("Initial state not set.");
    }
    return new StateMachineImpl(this);
  }
}
