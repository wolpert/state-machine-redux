package com.codeheadsystems.smr.yml;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.codeheadsystems.smr.StateMachineDefinition;
import org.junit.jupiter.api.Test;

class DefinitionTest extends TestBase {

  @Test
  void roundTrip() {
    Definition definition = Definition.disassemble(stateMachineDefinition);
    StateMachineDefinition reassembled = definition.assemble();
    assertThat(reassembled).isEqualTo(stateMachineDefinition);
  }

  @Test
  void missingInitialState() {
    Definition definition = Definition.disassemble(stateMachineDefinition);
    definition.setInitialState("missing");
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(definition::assemble);
  }

}