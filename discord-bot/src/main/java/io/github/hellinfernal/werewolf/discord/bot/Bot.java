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

import io.github.hellinfernal.werewolf.discord.bot.subscription.StartGameSubscription;
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
                        gateway.on(MessageCreateEvent.class, e ->  new StartGameSubscription(_discordClient, _gatewayDiscordClient, _gamesToBootstrapByChannel).handle(e)) //
                ))
                .block();
        /** _scheduler.scheduleAtFixedRate(this::checkGames, 30, 30, TimeUnit.SECONDS);
         * removed for test reasons.
         */
       //TODO: Remove Comment :D
    }

}
