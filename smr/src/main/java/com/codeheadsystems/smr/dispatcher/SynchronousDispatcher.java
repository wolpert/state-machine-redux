package com.codeheadsystems.smr.dispatcher;

import com.codeheadsystems.smr.Callback;
import com.codeheadsystems.smr.Context;
import com.codeheadsystems.smr.Dispatcher;
import com.codeheadsystems.smr.ImmutableCallback;
import com.codeheadsystems.smr.Phase;
import com.codeheadsystems.smr.State;
import com.codeheadsystems.smr.StateMachineDefinition;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SynchronousDispatcher implements Dispatcher {

  private static final Logger log = LoggerFactory.getLogger(SynchronousDispatcher.class);

  private final Map<State, Set<Consumer<Callback>>[]> callbackMap;

  public SynchronousDispatcher(final Set<State> states) {
    log.info("SynchronousDispatcher()");
    this.callbackMap = states.stream()
        .collect(HashMap::new, (map, state) -> map.put(state, buildList()), HashMap::putAll);
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
  public void handleTransitionEvent(final Context context,
                                    final State currentState,
                                    final State newState) {
    log.trace("handleTransitionEvent({}, {}, {})", context, currentState, newState);
    dispatchCallbacks(context, currentState, Phase.EXIT);
    final State previousState = changeState(context, currentState, newState);
    if (!previousState.equals(currentState)) {
      log.warn("handleTransitionEvent:state: {} != {}", previousState, currentState);
    }
    dispatchCallbacks(context, newState, Phase.ENTER);
  }

  @Override
  public State changeState(final Context context, final State currentState, final State newState) {
    return context.reference().getAndSet(newState);
  }

  @Override
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
      executeCallback(consumer, callback);
    });
  }

  @Override
  public void executeCallback(final Consumer<Callback> consumer, final Callback callback) {
    try {
      consumer.accept(callback);
    } catch (RuntimeException e) {
      log.error("dispatchCallbacks:error: {}", consumer, e);
    }
  }

  @SuppressWarnings("unchecked")
  private Set<Consumer<Callback>>[] buildList() {
    return Arrays.stream(Phase.values())
        .map(event -> new HashSet<Consumer<Callback>>()).toArray(Set[]::new);
  }

}
