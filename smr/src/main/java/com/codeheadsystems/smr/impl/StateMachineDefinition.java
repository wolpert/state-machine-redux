package com.codeheadsystems.smr.impl;

import com.codeheadsystems.smr.Event;
import com.codeheadsystems.smr.Context;
import com.codeheadsystems.smr.State;
import com.codeheadsystems.smr.StateMachineException;
import com.codeheadsystems.smr.callback.Callback;
import com.codeheadsystems.smr.callback.Phase;
import com.codeheadsystems.smr.callback.ImmutableCallback;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class StateMachineDefinition {

  private final Map<State, Map<Event, State>> transitions;
  private final Map<State, Set<Consumer<Callback>>[]> callbackMap;
  private final boolean useExceptions;
  private final State initialState;

  <T> StateMachineDefinition(final StateMachineDefinitionBuilder<T> builder) {
    if (builder.initialState == null) {
      throw new StateMachineException("Initial state is required.");
    }
    this.transitions = builder.transitions;
    this.initialState = builder.initialState;
    this.useExceptions = builder.useExceptions;
    this.callbackMap = states().stream()
        .collect(HashMap::new, (map, state) -> map.put(state, buildList()), HashMap::putAll);
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

  public void tick(final Context context) {
    final State currentState = context.reference().get();
    if (!transitions.containsKey(currentState)) {
      returnOrThrow(false, () -> new StateMachineException("State " + currentState + " is not in the state machine."));
    } else {
      dispatchCallbacks(context, currentState, Phase.TICK);
    }
  }

  public State dispatch(final Context context,
                        final Event event) {
    final State currentState = context.reference().get();
    if (!transitions.containsKey(currentState)) {
      return returnOrThrow(currentState, () -> new StateMachineException("State " + currentState + " is not in the state machine."));
    } else {
      final Map<Event, State> eventStateMap = transitions.get(currentState);
      final State newState = eventStateMap.get(event);
      if (newState != null) {
        dispatchCallbacks(context, currentState, Phase.EXIT);
        context.reference().set(newState);
        dispatchCallbacks(context, newState, Phase.ENTER);
        return newState;
      } else {
        return returnOrThrow(currentState,
            () -> new StateMachineException("No transition for event " + event + " from state " + currentState));
      }
    }
  }

  public void enable(final State state,
                     final Phase phase,
                     final Consumer<Callback> contextConsumer) {
    if (callbackMap.get(state) == null) {
      returnOrThrow(false, () -> new StateMachineException("State " + state + " is not in the state machine."));
    } else {
      callbackMap.get(state)[phase.ordinal()].add(contextConsumer);
    }
  }

  public void disable(final State state,
                      final Phase phase,
                      final Consumer<Callback> contextConsumer) {
    if (callbackMap.get(state) == null) {
      returnOrThrow(false, () -> new StateMachineException("State " + state + " is not in the state machine."));
    } else {
      callbackMap.get(state)[phase.ordinal()].remove(contextConsumer);
    }
  }

  private void dispatchCallbacks(final Context context,
                                 final State currentState,
                                 final Phase phase) {
    final Set<Consumer<Callback>>[] callbacks = callbackMap.get(currentState);
    final Callback callback = ImmutableCallback.builder()
        .context(context)
        .state(currentState)
        .event(phase)
        .build();
    // TODO: Do this safely
    callbacks[phase.ordinal()].forEach(consumer -> consumer.accept(callback));
  }

  private <T> T returnOrThrow(final T t,
                              final Supplier<StateMachineException> supplier) {
    if (useExceptions) {
      throw supplier.get();
    }
    return t;
  }

  @SuppressWarnings("unchecked")
  private Set<Consumer<Callback>>[] buildList() {
    return Arrays.stream(Phase.values())
        .map(event -> new HashSet<Consumer<Callback>>()).toArray(Set[]::new);
  }

  public static class Builder extends StateMachineDefinitionBuilder<StateMachineDefinition> {

    @Override
    public StateMachineDefinition build() {
      return new StateMachineDefinition(this);
    }

  }

}
