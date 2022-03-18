package io.github.hellinfernal.werewolf.discord.examples.button;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.reactivestreams.Publisher;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.core.spec.MessageEditSpec;
import discord4j.discordjson.json.ComponentData;
import discord4j.discordjson.json.MessageCreateRequest;
import io.github.hellinfernal.werewolf.discord.examples.BotExample;
import reactor.core.publisher.Mono;


public class ButtonDisableExample extends BotExample {

   private final Button _button1 = createButton("Button 1");
   private final Button _button2 = createButton("Button 2");
   private final AtomicLong _clicks = new AtomicLong();

   public static void main( String[] args ) {
      new ButtonDisableExample().example();
   }

   @Override
   protected Publisher<?> execute( GatewayDiscordClient gateway ) {
      return gateway.on(MessageCreateEvent.class, this::disableButtonHandler);

   }

   private Publisher<?> disableButtonHandler( MessageCreateEvent event ) {
      if (!event.getMessage().getContent().equalsIgnoreCase("!ButtonDisableExample")) {
         return Mono.empty();
      }

      return event.getMessage().getChannel().flatMap(channel ->
            channel.createMessage(MessageCreateSpec.builder()
                        .addComponent(ActionRow.of(_button1, _button2))
                  .build()
            ).then(event.getClient().on(ButtonInteractionEvent.class, buttonEvent -> {
               System.out.println("button clicked");
                  if(_clicks.incrementAndGet() ==5) {
                     return buttonEvent.getMessage()
                           .map(message -> message.edit(MessageEditSpec.builder()
                                       .addComponent(ActionRow.of(_button1.disabled(), _button2.disabled())).build())
                                 .then())
                           .orElse(Mono.empty());
                  }
                     return Mono.empty();
                  }
            ).then()
      ));
   }

   private Button createButton(final String name) {
      return Button.primary(UUID.randomUUID().toString(), name);
   }
 }
