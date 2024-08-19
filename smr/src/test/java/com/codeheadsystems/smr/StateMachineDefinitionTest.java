package com.codeheadsystems.smr;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StateMachineDefinitionTest extends TestBase {

  private StateMachineDefinition definition;

  @BeforeEach
  void setup() {
    final StateMachineDefinition.StateMachineDefinitionBuilder<?> builder = builder(false);
    definition = new StateMachineDefinition(builder);
  }

  @Test
  void initialState() {
    assertThat(definition.initialState()).isEqualTo(ONE);
  }

  @Test
  void states() {
    assertThat(definition.states()).containsExactlyInAnyOrder(ONE, TWO, THREE);
  }

  @Test
  void events() {
    assertThat(definition.events(ONE)).containsExactlyInAnyOrder(TO_TWO);
    assertThat(definition.events(TWO)).containsExactlyInAnyOrder(TO_THREE, TO_ONE);
    assertThat(definition.events(THREE)).containsExactlyInAnyOrder(TO_TWO);
  }

  @Test
  void hasState() {
    assertThat(definition.hasState(ONE)).isTrue();
    assertThat(definition.hasState(TWO)).isTrue();
    assertThat(definition.hasState(THREE)).isTrue();
    assertThat(definition.hasState(FOUR)).isFalse();
  }

  @Test
  void forEvent() {
    assertThat(definition.forEvent(ONE, TO_TWO)).contains(TWO);
    assertThat(definition.forEvent(ONE, TO_THREE)).isEmpty();
    assertThat(definition.forEvent(TWO, TO_THREE)).contains(THREE);
    assertThat(definition.forEvent(TWO, TO_ONE)).contains(ONE);
    assertThat(definition.forEvent(THREE, TO_TWO)).contains(TWO);
    assertThat(definition.forEvent(THREE, TO_ONE)).isEmpty();
  }

}