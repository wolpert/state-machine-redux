package com.codeheadsystems.smr.yml;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeheadsystems.smr.StateMachineDefinition;
import org.junit.jupiter.api.Test;

class YamlManagerTest extends TestBase {

  private final YamlManager yamlManager = new YamlManager();

  @Test
  void roundTrip() {
    String yaml = yamlManager.toYaml(stateMachineDefinition);
    StateMachineDefinition reassembled = yamlManager.fromYaml(yaml);
    assertThat(reassembled).isEqualTo(stateMachineDefinition);
  }

  // TODO: Add fixtures for tests in reading from yaml.
}