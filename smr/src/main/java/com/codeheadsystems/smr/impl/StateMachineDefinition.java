package com.codeheadsystems.smr.impl;

import com.codeheadsystems.smr.Event;
import com.codeheadsystems.smr.State;
import com.codeheadsystems.smr.StateMachineException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  public State initialState() {
    return initialState;
  }

  public Set<State> states() {
    return transitions.keySet();
  }

  public Set<Event> events(final State state) {
    return transitions.get(state).keySet();
  }

  public boolean hasState(final State state) {
    return transitions.containsKey(state);
  }

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
