package com.codeheadsystems.smr.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.codeheadsystems.smr.Action;
import com.codeheadsystems.smr.ImmutableAction;
import com.codeheadsystems.smr.ImmutableState;
import com.codeheadsystems.smr.State;
import com.codeheadsystems.smr.StateMachine;
import com.codeheadsystems.smr.StateMachineException;
import com.codeheadsystems.smr.callback.Callback;
import com.codeheadsystems.smr.callback.Event;
import com.codeheadsystems.smr.callback.ImmutableCallback;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

class StateMachineImplTest {

  private static final State ONE = ImmutableState.of("one");
  private static final State TWO = ImmutableState.of("two");
  private static final State THREE = ImmutableState.of("three");
  private static final Action TO_TWO = ImmutableAction.of("ToTwo");
  private static final Action TO_THREE = ImmutableAction.of("ToThree");
  private static final Action TO_ONE = ImmutableAction.of("ToOne");

  private static Callback getContext(final StateMachine stateMachine,
                                     final State state,
                                     final Event event) {
    return ImmutableCallback.builder()
        .stateMachine(stateMachine)
        .state(state)
        .event(event)
        .build();
  }

  StateMachine setUpStateMachine(boolean withException) {
    return StateMachine.builder()
        .withExceptions(withException)
        .addState(ONE).addState(TWO).addState(THREE)
        .setInitialState(ONE)
        .addTransition(ONE, TO_TWO, TWO)
        .addTransition(TWO, TO_THREE, THREE)
        .addTransition(THREE, TO_TWO, TWO)
        .addTransition(TWO, TO_ONE, ONE)
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
  void transition_withCallbacks() {
    StateMachine stateMachine = setUpStateMachine(false);
    Capture capture = new Capture();
    stateMachine.enable(ONE, Event.ENTER, capture::capture);
    stateMachine.enable(ONE, Event.EXIT, capture::capture);
    stateMachine.enable(TWO, Event.ENTER, capture::capture);
    stateMachine.enable(TWO, Event.EXIT, capture::capture);
    stateMachine.dispatch(TO_TWO);
    stateMachine.dispatch(TO_THREE);
    stateMachine.dispatch(TO_TWO);
    stateMachine.dispatch(TO_ONE);
    assertThat(capture.contexts).hasSize(6);
    assertThat(capture.contexts).containsExactly(
        getContext(stateMachine, ONE, Event.EXIT),
        getContext(stateMachine, TWO, Event.ENTER),
        getContext(stateMachine, TWO, Event.EXIT),
        getContext(stateMachine, TWO, Event.ENTER),
        getContext(stateMachine, TWO, Event.EXIT),
        getContext(stateMachine, ONE, Event.ENTER)
    );
  }

  @Test
  void ticks() {
    StateMachine stateMachine = setUpStateMachine(false);
    Callback expected = getContext(stateMachine, ONE, Event.TICK);
    Capture capture = new Capture();
    stateMachine.enable(ONE, Event.TICK, capture::capture);
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