package io.github.hellinfernal.werewolf.discord.bot;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.presence.Presence;
import discord4j.core.shard.LocalShardCoordinator;
import discord4j.core.shard.ShardingStrategy;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.store.jdk.JdkStoreService;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

public class Bot {


    public static void main(String[] args) {
        final Bot bot = new Bot();
        bot.start();
    }

    private DiscordClient _discordClient;
    GatewayDiscordClient _gatewayDiscordClient;
    private Map<Snowflake, GameBootstrap> _gamesToBootstrapByChannel = new HashMap<>();
    //TODO: scheduler/timer
    private ScheduledExecutorService _scheduler = Executors.newScheduledThreadPool(1);

    private void start() {
        _discordClient = DiscordClient.create(System.getenv("DISCORD_BOT_API_TOKEN"));
        _gatewayDiscordClient = _discordClient.gateway()
                .setSharding(ShardingStrategy.recommended())
                .setShardCoordinator(LocalShardCoordinator.create())
                .setAwaitConnections(true)
                .setEventDispatcher(EventDispatcher.buffering())
                .login()
         .block();
        _discordClient
                .withGateway(gateway -> Mono.when( //
                        gateway.on(MessageCreateEvent.class, this::startGame) //
                ))
                .block();
        /** _scheduler.scheduleAtFixedRate(this::checkGames, 30, 30, TimeUnit.SECONDS);
         * removed for test reasons.
         */
       //TODO: Remove Comment :D
    }

    private void checkGames() {
        //TODO: Remove COmment :D
        /**

        _gamesToBootstrapByChannel
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().get_started().isAfter(Instant.now().minusSeconds(30)))
        .forEach(entry -> entry.getValue().initiate(menuEvent)); **/

        //TODO: user über spielstart informieren
        //GameBootstrap bootstrap = null;
        //_discordClient.getChannelById(Snowflake.of(1)).
    }

    private Publisher<Void> startGame(final MessageCreateEvent event) {
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
