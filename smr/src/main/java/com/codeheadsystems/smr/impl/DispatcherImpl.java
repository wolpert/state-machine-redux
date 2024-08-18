package com.codeheadsystems.smr.impl;

import com.codeheadsystems.smr.Context;
import com.codeheadsystems.smr.State;
import com.codeheadsystems.smr.callback.Callback;
import com.codeheadsystems.smr.callback.ImmutableCallback;
import com.codeheadsystems.smr.callback.Phase;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class DispatcherImpl implements com.codeheadsystems.smr.Dispatcher {

  private final Map<State, Set<Consumer<Callback>>[]> callbackMap;
  private final boolean useExceptions;

  DispatcherImpl(final StateMachineDefinitionBuilder<?> builder) {
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
    callbackMap.get(state)[phase.ordinal()].add(contextConsumer);
  }

  @Override
  public void disable(final State state,
                      final Phase phase,
                      final Consumer<Callback> contextConsumer) {
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
    dispatchCallbacks(context, currentState, Phase.EXIT);
    context.reference().set(newState); // TODO: Gate this and validate old context is what we have.
    dispatchCallbacks(context, newState, Phase.ENTER);
  }

  public void dispatchCallbacks(final Context context,
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
