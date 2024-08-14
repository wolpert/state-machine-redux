package com.codeheadsystems.smr;

import org.immutables.value.Value;

@Value.Immutable
public interface State {

  @Value.Parameter
  String name();

}
