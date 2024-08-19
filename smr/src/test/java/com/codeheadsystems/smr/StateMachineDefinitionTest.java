package com.codeheadsystems.smr;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StateMachinestateMachineDefinitionTest extends TestBase {


  @Test
  void initialState() {
    assertThat(stateMachineDefinition.initialState()).isEqualTo(ONE);
  }

  @Test
  void states() {
    assertThat(stateMachineDefinition.states()).containsExactlyInAnyOrder(ONE, TWO, THREE);
  }

  @Test
  void events() {
    assertThat(stateMachineDefinition.events(ONE)).containsExactlyInAnyOrder(TO_TWO);
    assertThat(stateMachineDefinition.events(TWO)).containsExactlyInAnyOrder(TO_THREE, TO_ONE);
    assertThat(stateMachineDefinition.events(THREE)).containsExactlyInAnyOrder(TO_TWO);
  }

  @Test
  void hasState() {
    assertThat(stateMachineDefinition.hasState(ONE)).isTrue();
    assertThat(stateMachineDefinition.hasState(TWO)).isTrue();
    assertThat(stateMachineDefinition.hasState(THREE)).isTrue();
    assertThat(stateMachineDefinition.hasState(FOUR)).isFalse();
  }

  @Test
  void forEvent() {
    assertThat(stateMachineDefinition.forEvent(ONE, TO_TWO)).contains(TWO);
    assertThat(stateMachineDefinition.forEvent(ONE, TO_THREE)).isEmpty();
    assertThat(stateMachineDefinition.forEvent(TWO, TO_THREE)).contains(THREE);
    assertThat(stateMachineDefinition.forEvent(TWO, TO_ONE)).contains(ONE);
    assertThat(stateMachineDefinition.forEvent(THREE, TO_TWO)).contains(TWO);
    assertThat(stateMachineDefinition.forEvent(THREE, TO_ONE)).isEmpty();
  }

}