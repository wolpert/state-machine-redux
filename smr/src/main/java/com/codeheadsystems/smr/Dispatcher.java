package com.codeheadsystems.smr;

import com.codeheadsystems.smr.callback.Callback;
import com.codeheadsystems.smr.callback.Phase;
import java.util.function.Consumer;

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
