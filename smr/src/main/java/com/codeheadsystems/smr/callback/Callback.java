package com.codeheadsystems.smr.callback;

import com.codeheadsystems.smr.Context;
import com.codeheadsystems.smr.State;
import org.immutables.value.Value;

@Value.Immutable
public interface Callback {

  Phase event();

  State state();

  Context context();

}
