package com.codeheadsystems.smr;

import static org.slf4j.LoggerFactory.getLogger;

import com.codeheadsystems.smr.impl.DispatcherImpl;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.slf4j.Logger;

/**
 * State machines manage the current state(context) and transitions between states.
 * It uses a state machine definition to determine the valid transitions. The dispatcher
 * is used to handle the callbacks for transition listeners.
 */
public class StateMachine extends Context.Impl {

  private static final Logger log = getLogger(StateMachine.class);

  private final StateMachineDefinition definition;
  private final Dispatcher dispatcher;
  private final boolean useExceptions;

  StateMachine(final StateMachineDefinition definition,
               final Dispatcher dispatcher,
               final boolean useExceptions) {
    super(definition.initialState());
    log.info("StateMachineImpl():{}", definition.initialState());
    this.definition = definition;
    this.dispatcher = dispatcher;
    this.useExceptions = useExceptions;
  }

  public static StateMachine.Builder builder() {
    return new StateMachine.Builder();
  }

  /**
   * The current state of the state machine.
   *
   * @return the current state.
   */
  public State state() {
    return state.get();
  }

  /**
   * Get the states that are valid for the current state machine.
   *
   * @return set of states.
   */
  public Set<State> states() {
    return definition.states();
  }

  /**
   * Get the events that are valid for the current state.
   *
   * @return set of events
   */
  public Set<Event> events() {
    return events(state());
  }

  /**
   * Get the events that are valid for the given state.
   *
   * @param state to check.
   * @return set of event.
   */
  public Set<Event> events(final State state) {
    return definition.events(state);
  }

  public void enable(final State state,
                     final Phase phase,
                     final Consumer<Callback> contextConsumer) {
    if (definition.hasState(state)) {
      dispatcher.enable(state, phase, contextConsumer);
    } else {
      returnOrThrow(false, () -> new StateMachineException("State " + state + " is not in the state machine."));
    }
  }

  public void disable(final State state,
                      final Phase phase,
                      final Consumer<Callback> contextConsumer) {
    if (definition.hasState(state)) {
      dispatcher.disable(state, phase, contextConsumer);
    } else {
      returnOrThrow(false, () -> new StateMachineException("State " + state + " is not in the state machine."));
    }
  }

  /**
   * Dispatch an event to the state machine.
   *
   * @param event to dispatch.
   * @return the new state if changed. Else the current state.
   */
  public State dispatch(final Event event) {
    final State currentState = state();
    log.trace("dispatch({},{})", event, currentState);
    final Optional<State> optionalNewState = definition.forEvent(currentState, event);
    if (optionalNewState.isPresent()) {
      final State newState = optionalNewState.get();
      dispatcher.handleTransitionEvent(this, currentState, newState);
      return newState;
    } else {
      log.warn("No transition for event {} from state {}", event, currentState);
      return returnOrThrow(currentState,
          () -> new StateMachineException("No transition for event " + event + " from state " + currentState));
    }
  }

  /**
   * Tick the state machine. Basically causes the callbacks on the current
   * state to execute.
   */
  public void tick() {
    dispatcher.dispatchCallbacks(this, state(), Phase.TICK);
  }

  private <T> T returnOrThrow(final T t,
                              final Supplier<StateMachineException> supplier) {
    if (useExceptions) {
      throw supplier.get();
    }
    return t;
  }

  public static class Builder extends StateMachineDefinition.StateMachineDefinitionBuilder<StateMachine> {

    @Override
    public StateMachine build() {
      final StateMachineDefinition definition = new StateMachineDefinition(this);
      final Dispatcher dispatcher = new DispatcherImpl(states, useExceptions);
      return new StateMachine(definition, dispatcher, useExceptions);
    }

  }

}
