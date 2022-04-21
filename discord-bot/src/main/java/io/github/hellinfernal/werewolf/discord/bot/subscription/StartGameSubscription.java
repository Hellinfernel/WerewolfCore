package io.github.hellinfernal.werewolf.discord.bot.subscription;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.MessageCreateSpec;
import io.github.hellinfernal.werewolf.discord.bot.GameBootstrap;
import reactor.core.publisher.Mono;


public class StartGameSubscription implements Subscription<MessageCreateEvent> {

   private DiscordClient                       _discordClient;
   private GatewayDiscordClient                _gatewayDiscordClient;
   private final Map<Snowflake, GameBootstrap> _gamesToBootstrapByChannel;

   public StartGameSubscription( final DiscordClient discordClient, final GatewayDiscordClient gatewayDiscordClient, Map<Snowflake, GameBootstrap> gamesToBootstrapByChannel ) {
      _discordClient = discordClient;
      _gatewayDiscordClient = gatewayDiscordClient;
      _gamesToBootstrapByChannel = gamesToBootstrapByChannel;}

   @Override
   public Mono<Void> handle( MessageCreateEvent event ) {
      if (!event.getMessage().getContent().equalsIgnoreCase("!startGame")) {
         return Mono.empty();
      }

      return event.getMessage().getChannel().flatMap(c -> {
               if (_gamesToBootstrapByChannel.containsKey(c.getId())) {
                  return c.createMessage("game already started, wont start another one");
               } else {
                  final GameBootstrap bootstrap = new GameBootstrap(_discordClient, c.getId(), event.getGuildId().get(),_gatewayDiscordClient);
                  _gamesToBootstrapByChannel.put(c.getId(), bootstrap);
                  return c.createMessage(MessageCreateSpec.builder()
                              .content("Please click Join to join the game, game will start within the next 3 minutes.")
                              .addComponent(bootstrap.configureButtonsActionRow())
                              .build())
                        .then(generateGameMenu(event))
                        .timeout(Duration.ofMinutes(3))
                        .onErrorResume(TimeoutException.class, ignore -> {
                           //TODO: later
                           //channelGame.initiate();
                           return Mono.empty();
                        });


               }

            }
      ).then();
   }

   private Mono<Void> generateGameMenu(final MessageCreateEvent event) {
      return event.getClient().on(ButtonInteractionEvent.class, buttonEvent -> {
         final GameBootstrap bootstrap = _gamesToBootstrapByChannel
               .values()
               .stream()
               .filter(v -> v.hasButton(buttonEvent.getCustomId()))
               .findFirst()
               .orElse(null);
         //TODO: remove comment, this makes problems in the actual game

         /** if (bootstrap == null) {
          return buttonEvent
          .deferReply()
          .withEphemeral(true)
          .then(buttonEvent.createFollowup("Sorry, the game you tried to interact with, already started.")
          .withEphemeral(true))
          .then();} **/
         if (bootstrap != null){
            if (bootstrap.hasClickedRegister(buttonEvent.getCustomId()))
               return getJoinReaction(buttonEvent, bootstrap);
            if (bootstrap.hasClickedLeave(buttonEvent.getCustomId()))
               return getLeaveReaction(buttonEvent, bootstrap);
            if (bootstrap.hasClickedConfig(buttonEvent.getCustomId())) {
               return getConfigReaction(buttonEvent, bootstrap);
            }
            return Mono.empty();
         }
         return Mono.empty();
      }).then();
   }

   private Mono<Void> getConfigReaction(ButtonInteractionEvent buttonEvent, GameBootstrap bootstrap) {
      return bootstrap.configMenu(buttonEvent);
   }

   @org.jetbrains.annotations.NotNull
   private Mono<Void> getLeaveReaction(ButtonInteractionEvent buttonEvent, GameBootstrap bootstrap) {
      final Member member = buttonEvent.getInteraction().getMember().get();
      if (bootstrap.leave(member)) {
         return buttonEvent
               .deferReply()
               .then(Mono.just(bootstrap.configureButtonsEditSpec(buttonEvent.getMessage().get())))
               .then(buttonEvent.createFollowup(member
                     .getTag() + " was removed"))
               .then();
      } else {
         return buttonEvent
               .deferReply()
               .withEphemeral(true)
               .then(buttonEvent.createFollowup("You aren't in the game :D")
                     .withEphemeral(true))
               .then();
      }
   }

   @org.jetbrains.annotations.NotNull
   private Mono<Void> getJoinReaction(ButtonInteractionEvent buttonEvent, GameBootstrap bootstrap) {
      final Member member = buttonEvent.getInteraction().getMember().get();
      if (bootstrap.join(member)) {
         Supplier<Mono<Message>> gameStarted = Mono::empty;
         /**  if (bootstrap.hasReachedMinimumMembers()) {
          bootstrap.initiate(menuEvent);
          gameStarted = () -> buttonEvent
          .getMessage()
          .map(bootstrap::configureButtonEdit)
          .get()
          .then(buttonEvent.createFollowup("Game has started, have fun!"));
          } **/ //TODO: remove comment :D
         return buttonEvent
               .deferReply()
               .then(buttonEvent
                     .getMessage()
                     .map(bootstrap::configureButtonEdit)
                     .get())
               .then(buttonEvent.createFollowup(member
                     .getTag() + " was added"))
               .then(gameStarted.get())
               .then();
      } else {
         return buttonEvent
               .deferReply()
               .withEphemeral(true)
               .then(buttonEvent.createFollowup("You are already in the game :D")
                     .withEphemeral(true))
               .then();

      }
   }

}
