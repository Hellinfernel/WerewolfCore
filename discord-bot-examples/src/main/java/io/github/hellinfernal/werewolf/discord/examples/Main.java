package io.github.hellinfernal.werewolf.discord.examples;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.lifecycle.ConnectEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.TextChannel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

public class Main {
    static GatewayDiscordClient builder;
    private static final Map<String, Command> commands = new HashMap<>();
    public static final Snowflake alertChannel = Snowflake.of(947919993255895132L);
    // Wie und wann kommunizieren wir mit dem User?
    // -> DM ? Channel ? Vote ? Reactions?
    public static void main(String[] args) {

        builder = DiscordClientBuilder.create(System.getenv("DISCORD_BOT_API_TOKEN"))
                    .build()
                    .login()
                    .block();
        builder.getEventDispatcher().on(ReadyEvent.class).subscribe(event -> {
            User self = event.getSelf();
            System.out.println(String.format("Logged in as %s#%s", self.getUsername(), self.getDiscriminator()));
        });
        builder.getEventDispatcher().on(ConnectEvent.class).subscribe(event -> getAlertChannel()
                .flatMap(textChannel -> textChannel.createMessage("Connected")).then());

        addCommands();
        builder.getEventDispatcher().on(MessageCreateEvent.class)
                // 3.1 Message.getContent() is a String
                .flatMap(event -> Mono.just(event.getMessage().getContent())
                        .flatMap(content -> Flux.fromIterable(commands.entrySet())
                                // We will be using ! as our "prefix" to any command in the system.
                                .filter(entry -> content.startsWith('!' + entry.getKey()))
                                .flatMap(entry -> entry.getValue().execute(event))
                                .next()))
                .subscribe();






    }
    static void addCommands(){
        commands.put("ping", event -> event.getMessage().getChannel()
                .flatMap(channel -> channel.createMessage("Pong!"))
                .then());
    }
    static Mono<TextChannel> getAlertChannel(){
       return builder.getChannelById(alertChannel).ofType(TextChannel.class);
    }
}
