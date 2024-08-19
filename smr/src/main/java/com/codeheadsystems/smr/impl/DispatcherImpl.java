package com.codeheadsystems.smr.impl;

import com.codeheadsystems.smr.Context;
import com.codeheadsystems.smr.State;
import com.codeheadsystems.smr.Callback;
import com.codeheadsystems.smr.callback.ImmutableCallback;
import com.codeheadsystems.smr.Phase;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DispatcherImpl implements com.codeheadsystems.smr.Dispatcher {

  private static final Logger log = LoggerFactory.getLogger(DispatcherImpl.class);

  private final Map<State, Set<Consumer<Callback>>[]> callbackMap;
  private final boolean useExceptions;

  DispatcherImpl(final StateMachineDefinitionBuilder<?> builder) {
    log.info("DispatcherImpl()");
    this.useExceptions = builder.useExceptions;
    this.callbackMap = builder.states.stream()
        .collect(HashMap::new, (map, state) -> map.put(state, buildList()), HashMap::putAll);
  }

  public static DispatcherImpl.Builder builder() {
    return new DispatcherImpl.Builder();
  }

  @Override
  public void enable(final State state,
                     final Phase phase,
                     final Consumer<Callback> contextConsumer) {
    log.trace("enable({}, {}, {})", state, phase, contextConsumer);
    callbackMap.get(state)[phase.ordinal()].add(contextConsumer);
  }

  @Override
  public void disable(final State state,
                      final Phase phase,
                      final Consumer<Callback> contextConsumer) {
    log.trace("disable({}, {}, {})", state, phase, contextConsumer);
    callbackMap.get(state)[phase.ordinal()].remove(contextConsumer);
  }

  /**
   * TODO: This method needs to be handled with care. Need to consider if we want to 1) back out of events if
   * things failed, 2) keep it simple but incomplete, 3) allow for various implementations. (Most likely).
   *
   * @param context      that has state being changed.
   * @param currentState the from state.
   * @param newState     the too state.
   */
  @Override
  public void handleTransitionEvent(final Context context, final State currentState, final State newState) {
    log.trace("handleTransitionEvent({}, {}, {})", context, currentState, newState);
    dispatchCallbacks(context, currentState, Phase.EXIT);
    final State previousState = context.reference().getAndSet(newState);
    if (!previousState.equals(currentState)) {
      log.warn("handleTransitionEvent:state: {} != {}", previousState, currentState);
    }
    dispatchCallbacks(context, newState, Phase.ENTER);
  }

  public void dispatchCallbacks(final Context context,
                                final State currentState,
                                final Phase phase) {
    log.trace("dispatchCallbacks({}, {}, {})", context, currentState, phase);
    final Set<Consumer<Callback>>[] callbacks = callbackMap.get(currentState);
    final Callback callback = ImmutableCallback.builder()
        .context(context)
        .state(currentState)
        .phase(phase)
        .build();
    callbacks[phase.ordinal()].forEach(consumer -> {
      try {
        consumer.accept(callback);
      } catch (RuntimeException e) {
        log.error("dispatchCallbacks:error: {}", consumer, e);
      }
    });
  }

  @SuppressWarnings("unchecked")
  private Set<Consumer<Callback>>[] buildList() {
    return Arrays.stream(Phase.values())
        .map(event -> new HashSet<Consumer<Callback>>()).toArray(Set[]::new);
  }

  public static class Builder extends StateMachineDefinitionBuilder<DispatcherImpl> {

    @Override
    public DispatcherImpl build() {
      return new DispatcherImpl(this);
    }

  }

}
