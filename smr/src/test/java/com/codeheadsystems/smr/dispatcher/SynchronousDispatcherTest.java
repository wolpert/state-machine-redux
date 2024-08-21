package com.codeheadsystems.smr.dispatcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codeheadsystems.smr.Callback;
import com.codeheadsystems.smr.Context;
import com.codeheadsystems.smr.Dispatcher;
import com.codeheadsystems.smr.Phase;
import com.codeheadsystems.smr.TestBase;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SynchronousDispatcherTest extends TestBase {

  @Mock private Consumer<Callback> consumer;
  @Mock private Context context;
  @Captor private ArgumentCaptor<Callback> callback;

  private Dispatcher dispatcher;

  @BeforeEach
  void setUp() {
    dispatcher = new SynchronousDispatcher(stateMachineDefinition.states());
  }

  @Test
  void dispatchCallbacks_noException() {
    dispatcher.enable(ONE, Phase.TICK, consumer);
    dispatcher.dispatchCallbacks(context, ONE, Phase.TICK);
    verify(consumer).accept(callback.capture());
    assertThat(callback.getValue().context()).isEqualTo(context);
    assertThat(callback.getValue().state()).isEqualTo(ONE);
    assertThat(callback.getValue().phase()).isEqualTo(Phase.TICK);
  }

  @Test
  void dispatchCallbacks_withException() {
    doThrow(new RuntimeException("test")).when(consumer).accept(callback.capture());
    dispatcher.enable(ONE, Phase.TICK, consumer);
    dispatcher.dispatchCallbacks(context, ONE, Phase.TICK);
    assertThat(callback.getValue().context()).isEqualTo(context);
    assertThat(callback.getValue().state()).isEqualTo(ONE);
    assertThat(callback.getValue().phase()).isEqualTo(Phase.TICK);
  }

  @Test
  void handleTransitionEvent() {
    dispatcher.enable(ONE, Phase.EXIT, consumer);
    dispatcher.enable(TWO, Phase.ENTER, consumer);
    when(context.reference()).thenReturn(new AtomicReference<>(ONE));
    dispatcher.handleTransitionEvent(context, ONE, TWO);
    verify(consumer, times(2)).accept(callback.capture());
    assertThat(callback.getAllValues().get(0).context()).isEqualTo(context);
    assertThat(callback.getAllValues().get(0).state()).isEqualTo(ONE);
    assertThat(callback.getAllValues().get(0).phase()).isEqualTo(Phase.EXIT);
    assertThat(callback.getAllValues().get(1).context()).isEqualTo(context);
    assertThat(callback.getAllValues().get(1).state()).isEqualTo(TWO);
    assertThat(callback.getAllValues().get(1).phase()).isEqualTo(Phase.ENTER);
  }

  @Test
  void handleTransitionEvent_noFailWhenContextHasWrongeState() {
    dispatcher.enable(ONE, Phase.EXIT, consumer);
    dispatcher.enable(TWO, Phase.ENTER, consumer);
    when(context.reference()).thenReturn(new AtomicReference<>(THREE));
    dispatcher.handleTransitionEvent(context, ONE, TWO);
    verify(consumer, times(2)).accept(callback.capture());
    assertThat(callback.getAllValues().get(0).context()).isEqualTo(context);
    assertThat(callback.getAllValues().get(0).state()).isEqualTo(ONE);
    assertThat(callback.getAllValues().get(0).phase()).isEqualTo(Phase.EXIT);
    assertThat(callback.getAllValues().get(1).context()).isEqualTo(context);
    assertThat(callback.getAllValues().get(1).state()).isEqualTo(TWO);
    assertThat(callback.getAllValues().get(1).phase()).isEqualTo(Phase.ENTER);
  }


}