package com.codeheadsystems.smr.metrics;

import com.codeheadsystems.metrics.Metrics;
import com.codeheadsystems.metrics.Tags;
import com.codeheadsystems.smr.Callback;
import com.codeheadsystems.smr.Context;
import com.codeheadsystems.smr.Decorator;
import com.codeheadsystems.smr.Dispatcher;
import com.codeheadsystems.smr.Phase;
import com.codeheadsystems.smr.State;
import java.util.function.Consumer;

public class MetricsDecorator implements Decorator<Dispatcher> {

  private final Metrics metrics;

  public MetricsDecorator(final Metrics metrics) {
    this.metrics = metrics;
  }

  @Override
  public Dispatcher decorate(final Dispatcher dispatcher) {
    return new MetricsDispatcher(dispatcher, metrics);
  }

  public static class MetricsDispatcher implements Dispatcher {
    private final Dispatcher dispatcher;
    private final Metrics metrics;

    public MetricsDispatcher(final Dispatcher dispatcher, final Metrics metrics) {
      this.dispatcher = dispatcher;
      this.metrics = metrics;
    }

    @Override
    public void enable(final State state, final Phase phase, final Consumer<Callback> contextConsumer) {
      metrics.increment("dispatcher.enable", "state", state.name(), "phase", phase.name());
      dispatcher.enable(state, phase, contextConsumer);
    }

    @Override
    public void disable(final State state, final Phase phase, final Consumer<Callback> contextConsumer) {
      metrics.increment("dispatcher.disable", "state", state.name(), "phase", phase.name());
      dispatcher.disable(state, phase, contextConsumer);
    }

    @Override
    public void handleTransitionEvent(final Context context, final State currentState, final State newState) {
      final Tags tags = Tags.of("currentState", currentState.name(), "newState", newState.name(), "context", context.getClass().getSimpleName());
      metrics.time("dispatcher.handleTransitionEvent", tags, () -> {
        dispatcher.handleTransitionEvent(context, currentState, newState);
        return null;
      });
      dispatcher.handleTransitionEvent(context, currentState, newState);
    }

    @Override
    public State changeState(final Context context, final State currentState, final State newState) {
      return dispatcher.changeState(context, currentState, newState);
    }

    @Override
    public void dispatchCallbacks(final Context context, final State currentState, final Phase phase) {
      dispatcher.dispatchCallbacks(context, currentState, phase);
    }

    @Override
    public void executeCallback(final Consumer<Callback> consumer, final Callback callback) {
      final Tags tags = Tags.of("consumer", consumer.getClass().getSimpleName(),
          "state", callback.state().name(), "phase", callback.phase().name());
      metrics.time("dispatcher.executeCallback", tags, () -> {
        dispatcher.executeCallback(consumer, callback);
        return null;
      });
    }
  }

}
