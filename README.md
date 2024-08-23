# State Machine Redux

## Purpose

This project is a simplified state machine with add-ons
for complexity as needed. It is designed to be usable for
everything from games to business logic cases. It is a
light-weight framework that can be extended.

## Installation

All libraries are available on maven central.

| Library     | Purpose       | Version                                                                                       |
|-------------|---------------|-----------------------------------------------------------------------------------------------|
| smr         | Core Library  | ![State Machine Redux](https://img.shields.io/maven-central/v/com.codeheadsystems/smr)        |
| smr-metrics | Metrics addon | ![State Machine Redux](https://img.shields.io/maven-central/v/com.codeheadsystems/smr-metrics) |
| smr-yml     | YAML support  | ![State Machine Redux](https://img.shields.io/maven-central/v/com.codeheadsystems/smr-yml)    |


## Build out

The goal is to put together a state machine that is self-contained.
Once it has the features needed for it, then provide a way for the 
state machine to work with multiple contexts; each context has its own
existing state and a context can transition between states with the 
callbacks being notified. Though that complexity is not needed. You 
can simply use the state machine as a single state self-determined.
This is perfect for situations where there is only one in the system,
such as managing the state of an application starting, or a player
in a game.

But when you want to apply a state machine broadly to many objects, each
with their own state but they function the same, that's where we want
to model one state machine with many concurrent contexts.

The mistake with the original implementation was that this complex pattern
was built first, making it impossible to understand the simple use-case.

### TODO
1. ~~Ability to create the state machine definition from a file.~~
2. ~~Hooks at each stage.~~
   1. ~~Metrics extension~~
3. ~~Publish library to maven~~
4. Consider a State Machine Engine to handle multiple contexts for the same state machine.

## Example

Simple example where we focus on a single state machine and the context is defined
in the code.

```java
  // Setup State machine
   StateMachine sm = StateMachine.builder()
       .addState(IDLE).addState(RUN).addState(JUMP)
       .setInitialState(IDLE)
       .addTransition(IDLE, SPACEBAR, JUMP)
       .addTransition(RUN, SPACEBAR, JUMP)
       .addTransition(JUMP, END_ACTION, IDLE)
       .addTransition(RUN, END_ACTION, IDLE)
       .addTransition(IDLE, ACTION_BUTTON, RUN)
       .build();

    sm.enable(IDLE, Event.TICK, (c)->idleModeDisplay());
    sm.enable(RUN, Event.TICK, (c)->runModeDisplay());
    sm.enable(RUN, Event.TICK, (c)->runModeDisplay());
    sm.enable(JUMP, Event.ENTER, (c)->startAnimation("jump"));
    sm.enable(RUN, Event.ENTER, (c)->startAnimation("run"));
```

An example where the context for the state that is changing is external to the
state machine: