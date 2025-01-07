package com.codeheadsystems.smr;


public class TestBase {

  public static final State ONE = State.of("one");
  public static final State TWO = State.of("two");
  public static final State THREE = State.of("three");
  public static final State FOUR = State.of("four");
  public static final Event TO_TWO = Event.of("ToTwo");
  public static final Event TO_THREE = Event.of("ToThree");
  public static final Event TO_ONE = Event.of("ToOne");

  protected StateMachineDefinition stateMachineDefinition = StateMachineDefinition.builder()
      .addState(ONE).addState(TWO).addState(THREE)
      .setInitialState(ONE)
      .addTransition(ONE, TO_TWO, TWO)
      .addTransition(TWO, TO_THREE, THREE)
      .addTransition(THREE, TO_TWO, TWO)
      .addTransition(TWO, TO_ONE, ONE)
      .build();


  public StateMachine setUpStateMachine(boolean withException, Decorator<Dispatcher>... decorators) {
    StateMachine.Builder builder = StateMachine.builder();
    for (Decorator<Dispatcher> decorator : decorators) {
      builder.withDispatcherDecorator(decorator);
    }
    return builder.withUseExceptions(withException)
        .withStateMachineDefinition(stateMachineDefinition)
        .build();
  }

}
