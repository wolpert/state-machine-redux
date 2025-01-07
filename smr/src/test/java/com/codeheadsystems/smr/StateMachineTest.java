package com.codeheadsystems.smr;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.ArrayList;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;

class StateMachineTest extends TestBase {


  private static Callback getContext(final StateMachine stateMachine,
                                     final State state,
                                     final Phase phase) {
    return ImmutableCallback.builder()
        .context(stateMachine)
        .state(state)
        .phase(phase)
        .build();
  }

  @Test
  void initialState() {
    StateMachine stateMachine = setUpStateMachine(false);
    assertThat(stateMachine.state()).isEqualTo(ONE);
  }

  @Test
  void transition() {
    StateMachine stateMachine = setUpStateMachine(false);
    assertThat(stateMachine.dispatch(TO_TWO)).isEqualTo(TWO);
    assertThat(stateMachine.state()).isEqualTo(TWO);
    assertThat(stateMachine.dispatch(TO_THREE)).isEqualTo(THREE);
    assertThat(stateMachine.state()).isEqualTo(THREE);
    assertThat(stateMachine.dispatch(TO_TWO)).isEqualTo(TWO);
    assertThat(stateMachine.state()).isEqualTo(TWO);
    assertThat(stateMachine.dispatch(TO_ONE)).isEqualTo(ONE);
    assertThat(stateMachine.state()).isEqualTo(ONE);
  }

  @Test
  void transition_cannotSkipTwo() {
    StateMachine stateMachine = setUpStateMachine(false);
    assertThat(stateMachine.state()).isEqualTo(ONE);
    assertThat(stateMachine.dispatch(TO_THREE)).isEqualTo(ONE);
    assertThat(stateMachine.state()).isEqualTo(ONE);
    assertThat(stateMachine.dispatch(TO_TWO)).isEqualTo(TWO);
    assertThat(stateMachine.state()).isEqualTo(TWO);
  }

  @Test
  void transition_cannotSkipTwo_withException() {
    StateMachine stateMachine = setUpStateMachine(true);
    assertThat(stateMachine.state()).isEqualTo(ONE);
    assertThatExceptionOfType(StateMachineException.class)
        .isThrownBy(() -> stateMachine.dispatch(TO_THREE));
    assertThat(stateMachine.state()).isEqualTo(ONE);
    assertThat(stateMachine.dispatch(TO_TWO)).isEqualTo(TWO);
    assertThat(stateMachine.state()).isEqualTo(TWO);
  }

  @Test
  void transitionsFailsToUnknownEvent() {
    StateMachine stateMachine = setUpStateMachine(true);
    assertThat(stateMachine.state()).isEqualTo(ONE);
    assertThatExceptionOfType(StateMachineException.class)
        .isThrownBy(() -> stateMachine.dispatch(Event.of("unknown")));
    assertThat(stateMachine.state()).isEqualTo(ONE);
  }

  @Test
  void transition_withCallbacks() {
    StateMachine stateMachine = setUpStateMachine(false);
    Capture capture = new Capture();
    stateMachine.enable(ONE, Phase.ENTER, capture::capture);
    stateMachine.enable(ONE, Phase.EXIT, capture::capture);
    stateMachine.enable(TWO, Phase.ENTER, capture::capture);
    stateMachine.enable(TWO, Phase.EXIT, capture::capture);
    stateMachine.dispatch(TO_TWO);
    stateMachine.dispatch(TO_THREE);
    stateMachine.dispatch(TO_TWO);
    stateMachine.dispatch(TO_ONE);
    assertThat(capture.contexts).hasSize(6);
    assertThat(capture.contexts).containsExactly(
        getContext(stateMachine, ONE, Phase.EXIT),
        getContext(stateMachine, TWO, Phase.ENTER),
        getContext(stateMachine, TWO, Phase.EXIT),
        getContext(stateMachine, TWO, Phase.ENTER),
        getContext(stateMachine, TWO, Phase.EXIT),
        getContext(stateMachine, ONE, Phase.ENTER)
    );
  }

  @Test
  void ticks() {
    StateMachine stateMachine = setUpStateMachine(false);
    Callback expected = getContext(stateMachine, ONE, Phase.TICK);
    Capture capture = new Capture();
    stateMachine.enable(ONE, Phase.TICK, capture::capture);
    stateMachine.tick();
    stateMachine.tick();
    assertThat(capture.contexts).hasSize(2);
    assertThat(capture.contexts).containsExactly(expected, expected);
  }

  @Test
  void ticks_withDecorator() {
    StateMachine stateMachine = setUpStateMachine(false, new TestDecorator(0), new TestDecorator(1));
    Callback expected = getContext(stateMachine, ONE, Phase.TICK);
    Capture capture = new Capture();
    stateMachine.enable(ONE, Phase.TICK, capture::capture);
    stateMachine.tick();
    stateMachine.tick();
    assertThat(capture.contexts).hasSize(2);
    assertThat(capture.contexts).containsExactly(expected, expected);
    assertThat(TestDecorator.ordering()).isEqualTo("1010"); // last decorator is hit first.
  }

  static class TestDecorator implements Decorator<Dispatcher>, Dispatcher {
    private static StringBuilder ordering = new StringBuilder();
    private final int myCount;
    private Dispatcher original;

    TestDecorator(int myCount) {
      this.myCount = myCount;
    }

    @Override
    public Dispatcher decorate(final Dispatcher dispatcher) {
      original = dispatcher;
      return this;
    }

    public static String ordering() {
      return ordering.toString();
    }

    @Override
    public void enable(final State state, final Phase phase, final Consumer<Callback> contextConsumer) {
      original.enable(state, phase, contextConsumer);
    }

    @Override
    public void disable(final State state, final Phase phase, final Consumer<Callback> contextConsumer) {
      original.disable(state, phase, contextConsumer);
    }

    @Override
    public void handleTransitionEvent(final Context context, final State currentState, final State newState) {
      original.handleTransitionEvent(context, currentState, newState);
    }

    @Override
    public State changeState(final Context context, final State currentState, final State newState) {
      return original.changeState(context, currentState, newState);
    }

    @Override
    public void dispatchCallbacks(final Context context, final State currentState, final Phase phase) {
      ordering.append(myCount);
      original.dispatchCallbacks(context, currentState, phase);
    }

    @Override
    public void executeCallback(final Consumer<Callback> consumer, final Callback callback) {
      original.executeCallback(consumer, callback);
    }
  }

  static class Capture {

    ArrayList<Callback> contexts = new ArrayList<>();

    public void capture(Callback context) {
      contexts.add(context);
    }
  }

}