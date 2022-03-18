package io.github.hellinfernal.werewolf.discord.examples;

import org.reactivestreams.Publisher;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;


public abstract class BotExample {

   protected final DiscordClient _discordClient;

   public BotExample() {
      _discordClient = DiscordClient.create("OTI4NjE4Njk5NTYzMzUyMDc0.YdbZjg.cU5V_ICw-_3mna8iyM70nOhi4yg");
   }

   public void example() {
      _discordClient
            .withGateway(this::execute)
            .block();
   }

   protected abstract Publisher<?> execute( GatewayDiscordClient gateway );
}
