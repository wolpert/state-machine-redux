package com.codeheadsystems.smr.yml;

import com.codeheadsystems.smr.StateMachineDefinition;
import com.codeheadsystems.smr.StateMachineException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class YamlManager {

  private final ObjectMapper objectMapper;

  public YamlManager() {
    this(new ObjectMapper(new YAMLFactory()));
  }

  public YamlManager(final ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
    this.objectMapper.findAndRegisterModules();
  }

  public String toYaml(final StateMachineDefinition definition) {
    try {
      return objectMapper.writeValueAsString(Definition.disassemble(definition));
    } catch (Exception e) {
      throw new StateMachineException("Failed to convert to yaml.", e);
    }
  }

  public StateMachineDefinition fromYaml(final String yaml) {
    try {
      final Definition definition = objectMapper.readValue(yaml, Definition.class);
      return definition.assemble();
    } catch (Exception e) {
      throw new StateMachineException("Failed to convert from yaml.", e);
    }
  }

}
