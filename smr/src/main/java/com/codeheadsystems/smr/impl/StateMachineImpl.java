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

  private final StateMachineDefinition definition;

  StateMachineImpl(final StateMachineDefinition definition) {
    super(definition.initialState());
    this.definition = definition;
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
  public Set<Action> actions() {
    return actions(state());
  }

  @Override
  public Set<Action> actions(final State state) {
    return definition.actions(state);
  }

  @Override
  public void tick() {
    definition.tick(this);
  }

  @Override
  public State dispatch(final Action action) {
    return definition.dispatch(this, action);
  }

  @Override
  public void enable(final State state,
                     final Event event,
                     final Consumer<Callback> contextConsumer) {
    definition.enable(state, event, contextConsumer);
  }

  @Override
  public void disable(final State state,
                      final Event event,
                      final Consumer<Callback> contextConsumer) {
    definition.disable(state, event, contextConsumer);
  }


  public static class Builder extends StateMachineDefinitionBuilder<StateMachine> {

    @Override
    public StateMachine build() {
      return new StateMachineImpl(new StateMachineDefinition(this));
    }

  }
}
