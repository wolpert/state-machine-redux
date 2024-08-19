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

  void handleTransitionEvent(Context context, State currentState, State newState);

  void dispatchCallbacks(Context context,
                         State currentState,
                         Phase phase);
}
