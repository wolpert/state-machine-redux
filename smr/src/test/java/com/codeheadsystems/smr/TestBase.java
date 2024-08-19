package com.codeheadsystems.smr;

import com.codeheadsystems.smr.impl.StateMachineDefinitionBuilder;

public class TestBase {

  public static final State ONE = ImmutableState.of("one");
  public static final State TWO = ImmutableState.of("two");
  public static final State THREE = ImmutableState.of("three");
  public static final State FOUR = ImmutableState.of("four");
  public static final Event TO_TWO = ImmutableEvent.of("ToTwo");
  public static final Event TO_THREE = ImmutableEvent.of("ToThree");
  public static final Event TO_ONE = ImmutableEvent.of("ToOne");

  public StateMachineDefinitionBuilder<StateMachine> builder(boolean withException) {
    return StateMachine.builder()
        .withExceptions(withException)
        .addState(ONE).addState(TWO).addState(THREE)
        .setInitialState(ONE)
        .addTransition(ONE, TO_TWO, TWO)
        .addTransition(TWO, TO_THREE, THREE)
        .addTransition(THREE, TO_TWO, TWO)
        .addTransition(TWO, TO_ONE, ONE);
  }

  public StateMachine setUpStateMachine(boolean withException) {
    return builder(withException).build();
  }

}
