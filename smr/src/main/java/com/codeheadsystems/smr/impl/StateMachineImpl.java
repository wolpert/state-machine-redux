package com.codeheadsystems.smr.impl;

import com.codeheadsystems.smr.Action;
import com.codeheadsystems.smr.Context;
import com.codeheadsystems.smr.State;
import com.codeheadsystems.smr.StateMachine;
import com.codeheadsystems.smr.StateMachineException;
import com.codeheadsystems.smr.callback.Callback;
import com.codeheadsystems.smr.callback.Event;
import com.codeheadsystems.smr.callback.ImmutableCallback;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class StateMachineImpl implements StateMachine, Context {

  private final AtomicReference<State> state;
  private final Map<State, Map<Action, State>> transitions;
  private final Map<State, Set<Consumer<Callback>>[]> callbackMap;
  private final boolean useExceptions;

  StateMachineImpl(final StateMachineBuilder builder) {
    this.state = new AtomicReference<>(builder.initialState);
    this.transitions = builder.transitions;
    this.useExceptions = builder.useExceptions;
    this.callbackMap = states().stream()
        .collect(HashMap::new, (map, state) -> map.put(state, buildList()), HashMap::putAll);
  }

  @Override
  public AtomicReference<State> reference() {
    return state;
  }

  @Override
  public State state() {
    return state.get();
  }

  @Override
  public Set<State> states() {
    return transitions.keySet();
  }

  @Override
  public Set<Action> actions() {
    return actions(state.get());
  }

  @Override
  public Set<Action> actions(final State state) {
    return transitions.get(state).keySet();
  }

  @Override
  public void tick() {
    final State currentState = state.get();
    dispatchCallbacks(currentState, Event.TICK);
  }

  @Override
  public State dispatch(final Action action) {
    final State currentState = state.get();
    final Map<Action, State> actionStateMap = transitions.get(currentState);
    final State newState = actionStateMap.get(action);
    if (newState != null) {
      dispatchCallbacks(currentState, Event.EXIT);
      state.set(newState);
      dispatchCallbacks(newState, Event.ENTER);
      return newState;
    }
    return returnOrThrow(currentState,
        () -> new StateMachineException("No transition for action " + action + " from state " + currentState));
  }

  @Override
  public void enable(final State state,
                     final Event event,
                     final Consumer<Callback> contextConsumer) {
    callbackMap.get(state)[event.ordinal()].add(contextConsumer);
  }

  @Override
  public void disable(final State state,
                      final Event event,
                      final Consumer<Callback> contextConsumer) {
    callbackMap.get(state)[event.ordinal()].remove(contextConsumer);
  }

  private void dispatchCallbacks(final State currentState,
                                 final Event event) {
    final Set<Consumer<Callback>>[] callbacks = callbackMap.get(currentState);
    final Callback context = ImmutableCallback.builder()
        .stateMachine(this)
        .state(currentState)
        .event(event)
        .build();
    // TODO: Do this safely
    callbacks[event.ordinal()].forEach(consumer -> consumer.accept(context));
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
    return Arrays.stream(Event.values())
        .map(event -> new HashSet<Consumer<Callback>>()).toArray(Set[]::new);
  }

}
