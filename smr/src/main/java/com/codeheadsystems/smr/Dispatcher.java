package com.codeheadsystems.smr;

import java.util.function.Consumer;

/**
 * Defines how we dispatch callbacks for transitions within a state machine.
 * It is customizable so we allow for multiple callbacks.
 */
public interface Dispatcher {
  void enable(State state,
              Phase phase,
              Consumer<Callback> contextConsumer);

  void disable(State state,
               Phase phase,
               Consumer<Callback> contextConsumer);

  /**
   * Macro method that handles the full state change and callback execution.
   *
   * @param context that holds onto the current state.
   * @param currentState expected current state.
   * @param newState new state to call.
   */
  void handleTransitionEvent(Context context, State currentState, State newState);

  /**
   * Does the state change.
   * @param context that holds onto the current state.
   * @param newState the new state for the context.
   * @return the actual state the context had. It's possible its different than the current state.
   */
  State changeState(final Context context, final State currentState, final State newState);

  /**
   * Dispatches the callbacks for the given state and phase to all listeners. This builds the callback object.
   * @param context that holds onto the current state.
   * @param currentState the current state.
   * @param phase the phase of the transition.
   */
  void dispatchCallbacks(Context context,
                         State currentState,
                         Phase phase);

  /**
   * Executes the callback.
   * @param consumer to call with the callback.
   * @param callback the callback itself.
   */
  void executeCallback(final Consumer<Callback> consumer, final Callback callback);
}
