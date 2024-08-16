package com.codeheadsystems.smr.callback;

import com.codeheadsystems.smr.State;
import com.codeheadsystems.smr.StateMachine;
import org.immutables.value.Value;

@Value.Immutable
public interface Callback {

  StateMachine stateMachine();

  Event event();

  State state();

}
