package com.codeheadsystems.smr;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.codeheadsystems.smr.callback.ImmutableCallback;
import java.util.ArrayList;
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
        .isThrownBy(() -> stateMachine.dispatch(ImmutableEvent.of("unknown")));
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

  static class Capture {

    ArrayList<Callback> contexts = new ArrayList<>();

    public void capture(Callback context) {
      contexts.add(context);
    }
  }

}