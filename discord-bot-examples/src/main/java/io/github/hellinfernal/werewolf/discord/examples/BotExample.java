package io.github.hellinfernal.werewolf.discord.examples;

import org.reactivestreams.Publisher;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;


public abstract class BotExample {

   protected final DiscordClient _discordClient;

   public BotExample() {
      _discordClient = DiscordClient.create(System.getenv("DISCORD_BOT_API_TOKEN"));
   }

   public void example() {
      _discordClient
            .withGateway(this::execute)
            .block();
   }

   protected abstract Publisher<?> execute( GatewayDiscordClient gateway );
}
