package com.codeheadsystems.smr;

import org.immutables.value.Value;
import org.immutables.value.Value.Immutable;

@Immutable
public interface Event {

  @Value.Parameter
  String name();

}
