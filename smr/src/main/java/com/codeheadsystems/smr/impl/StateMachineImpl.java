package com.codeheadsystems.smr.impl;

import com.codeheadsystems.smr.Context;
import com.codeheadsystems.smr.Dispatcher;
import com.codeheadsystems.smr.Event;
import com.codeheadsystems.smr.State;
import com.codeheadsystems.smr.StateMachine;
import com.codeheadsystems.smr.StateMachineException;
import com.codeheadsystems.smr.callback.Callback;
import com.codeheadsystems.smr.callback.Phase;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class StateMachineImpl extends Context.Impl implements StateMachine {

  private final StateMachineDefinition definition;
  private final Dispatcher dispatcher;
  private final boolean useExceptions;

  StateMachineImpl(final StateMachineDefinition definition,
                   final Dispatcher dispatcher,
                   final boolean useExceptions) {
    super(definition.initialState());
    this.definition = definition;
    this.dispatcher = dispatcher;
    this.useExceptions = useExceptions;
  }

  @Override
  public State state() {
    return state.get();
  }

  @Override
  public Set<State> states() {
    return definition.states();
  }

  @Override
  public Set<Event> events() {
    return events(state());
  }

  @Override
  public Set<Event> events(final State state) {
    return definition.events(state);
  }

  @Override
  public void tick() {
    dispatcher.dispatchCallbacks(this, state(), Phase.TICK);
  }

  @Override
  public State dispatch(final Event event) {
    final State currentState = state();
    final Optional<State> optionalNewState = definition.forEvent(currentState, event);
    if (optionalNewState.isPresent()) {
      final State newState = optionalNewState.get();
      dispatcher.handleTransitionEvent(this, currentState, newState);
      return newState;
    } else {
      return returnOrThrow(currentState,
          () -> new StateMachineException("No transition for event " + event + " from state " + currentState));
    }
  }

  @Override
  public void enable(final State state,
                     final Phase phase,
                     final Consumer<Callback> contextConsumer) {
    if (definition.hasState(state)) {
      dispatcher.enable(state, phase, contextConsumer);
    } else {
      returnOrThrow(false, () -> new StateMachineException("State " + state + " is not in the state machine."));
    }
  }

  @Override
  public void disable(final State state,
                      final Phase phase,
                      final Consumer<Callback> contextConsumer) {
    if (definition.hasState(state)) {
      dispatcher.disable(state, phase, contextConsumer);
    } else {
      returnOrThrow(false, () -> new StateMachineException("State " + state + " is not in the state machine."));
    }
  }

  private <T> T returnOrThrow(final T t,
                              final Supplier<StateMachineException> supplier) {
    if (useExceptions) {
      throw supplier.get();
    }
    return t;
  }

  public static class Builder extends StateMachineDefinitionBuilder<StateMachine> {

    @Override
    public StateMachine build() {
      final StateMachineDefinition definition = new StateMachineDefinition(this);
      final Dispatcher dispatcher = new DispatcherImpl(this);
      return new StateMachineImpl(definition, dispatcher, useExceptions);
    }

  }
}
