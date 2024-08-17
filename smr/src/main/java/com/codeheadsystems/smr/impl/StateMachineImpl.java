package com.codeheadsystems.smr.impl;

import com.codeheadsystems.smr.Action;
import com.codeheadsystems.smr.Context;
import com.codeheadsystems.smr.State;
import com.codeheadsystems.smr.StateMachine;
import com.codeheadsystems.smr.callback.Callback;
import com.codeheadsystems.smr.callback.Event;
import java.util.Set;
import java.util.function.Consumer;

public class StateMachineImpl extends Context.Impl implements StateMachine {

  private final StateMachineRuntime runtime;

  StateMachineImpl(final StateMachineBuilder builder) {
    super(builder.initialState);
    runtime = new StateMachineRuntime(builder);
  }

  @Override
  public State state() {
    return reference().get();
  }

  @Override
  public Set<State> states() {
    return runtime.states();
  }

  @Override
  public Set<Action> actions() {
    return actions(state());
  }

  @Override
  public Set<Action> actions(final State state) {
    return runtime.actions(state);
  }

  @Override
  public void tick() {
    runtime.tick(this);
  }

  @Override
  public State dispatch(final Action action) {
    return runtime.dispatch(this, action);
  }

  @Override
  public void enable(final State state,
                     final Event event,
                     final Consumer<Callback> contextConsumer) {
    runtime.enable(state, event, contextConsumer);
  }

  @Override
  public void disable(final State state,
                      final Event event,
                      final Consumer<Callback> contextConsumer) {
    runtime.disable(state, event, contextConsumer);
  }

}
