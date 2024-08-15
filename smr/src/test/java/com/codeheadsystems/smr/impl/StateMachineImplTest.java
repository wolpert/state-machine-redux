package com.codeheadsystems.smr.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.codeheadsystems.smr.Action;
import com.codeheadsystems.smr.CallbackContext;
import com.codeheadsystems.smr.ImmutableAction;
import com.codeheadsystems.smr.ImmutableState;
import com.codeheadsystems.smr.State;
import com.codeheadsystems.smr.StateMachine;
import com.codeheadsystems.smr.StateMachineException;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

class StateMachineImplTest {

  private static final State ONE = ImmutableState.of("one");
  private static final State TWO = ImmutableState.of("two");
  private static final State THREE = ImmutableState.of("three");
  private static final Action TO_TWO = ImmutableAction.of("ToTwo");
  private static final Action TO_THREE = ImmutableAction.of("ToThree");
  private static final Action TO_ONE = ImmutableAction.of("ToOne");

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
  void ticks() {
    StateMachine stateMachine = setUpStateMachine(false);
    Capture capture = new Capture();
    stateMachine.enableCallback(ONE, CallbackContext.Event.TICK, capture::capture);
    stateMachine.tick();
    stateMachine.tick();
    assertThat(capture.contexts).hasSize(2);
    assertThat(capture.contexts.get(0).stateMachine()).isEqualTo(stateMachine);
    assertThat(capture.contexts.get(0).event()).isEqualTo(CallbackContext.Event.TICK);
    assertThat(capture.contexts.get(0).state()).isEqualTo(ONE);
    assertThat(capture.contexts.get(1).stateMachine()).isEqualTo(stateMachine);
    assertThat(capture.contexts.get(1).event()).isEqualTo(CallbackContext.Event.TICK);
    assertThat(capture.contexts.get(1).state()).isEqualTo(ONE);
  }

  static class Capture {

    ArrayList<CallbackContext> contexts = new ArrayList<>();

    public void capture(CallbackContext context) {
      contexts.add(context);
    }
  }

}