package com.codeheadsystems.smr.impl;

import com.codeheadsystems.smr.Event;
import com.codeheadsystems.smr.Context;
import com.codeheadsystems.smr.State;
import com.codeheadsystems.smr.StateMachine;
import com.codeheadsystems.smr.callback.Callback;
import com.codeheadsystems.smr.callback.Phase;
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
  public Set<Event> events() {
    return events(state());
  }

  @Override
  public Set<Event> events(final State state) {
    return definition.events(state);
  }

  @Override
  public void tick() {
    definition.tick(this);
  }

  @Override
  public State dispatch(final Event event) {
    return definition.dispatch(this, event);
  }

  @Override
  public void enable(final State state,
                     final Phase phase,
                     final Consumer<Callback> contextConsumer) {
    definition.enable(state, phase, contextConsumer);
  }

  @Override
  public void disable(final State state,
                      final Phase phase,
                      final Consumer<Callback> contextConsumer) {
    definition.disable(state, phase, contextConsumer);
  }


  public static class Builder extends StateMachineDefinitionBuilder<StateMachine> {

    @Override
    public StateMachine build() {
      return new StateMachineImpl(new StateMachineDefinition(this));
    }

  }
}
