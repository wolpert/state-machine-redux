package com.codeheadsystems.smr.impl;

import com.codeheadsystems.smr.Action;
import com.codeheadsystems.smr.State;
import com.codeheadsystems.smr.StateMachine;
import com.codeheadsystems.smr.StateMachineException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class StateMachineImpl implements StateMachine {

  private final AtomicReference<State> state;
  private final Map<State, Map<Action, State>> transitions;
  private final boolean useExceptions;

  StateMachineImpl(final StateMachineBuilder builder) {
    this.state = new AtomicReference<>(builder.initialState);
    this.transitions = builder.transitions;
    this.useExceptions = builder.useExceptions;
  }

  @Override
  public State state() {
    return state.get();
  }

  @Override
  public State dispatch(final Action action) {
    final State currentState = state.get();
    final Map<Action, State> actionStateMap = transitions.get(currentState);
    final State newState = actionStateMap.get(action);
    if (newState != null) {
      state.set(newState);
      return newState;
    }
    return returnOrThrow(currentState, () -> new StateMachineException("No transition for action " + action + " from state " + currentState));
  }

  private <T> T returnOrThrow(final T t, final Supplier<StateMachineException> supplier) {
    if (useExceptions) {
      throw supplier.get();
    }
    return t;
  }

}
