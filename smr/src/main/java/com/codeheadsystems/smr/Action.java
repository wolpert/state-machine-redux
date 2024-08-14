package com.codeheadsystems.smr;

import org.immutables.value.Value;
import org.immutables.value.Value.Immutable;

@Immutable
public interface Action {

  @Value.Parameter
  String name();

}
