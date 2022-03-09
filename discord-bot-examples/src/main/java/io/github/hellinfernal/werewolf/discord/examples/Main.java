package io.github.hellinfernal.werewolf.discord.examples;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.lifecycle.*;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.awt.*;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    static GatewayDiscordClient builder;
    private static final Map<String, Command> commands = new HashMap<>();
    public static final Snowflake alertChannel = Snowflake.of(947919993255895132L);
   // public static final Map<ApplicationCommandRequest, SlashCommand> slashCommands = new ArrayList<>();
    static List<ApplicationCommandRequest> slashCommands = new ArrayList<>();
    static long applicationId;
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
        slashCommands.add(ApplicationCommandRequest.builder()
                .name("ping")
                .description("Makes a Pong")
                .build());
        slashCommands.add(ApplicationCommandRequest.builder()
                .name("greet")
                .description("Greets you")
                .addOption(ApplicationCommandOptionData.builder()
                        .name("name")
                        .description("Your name")
                        .type(ApplicationCommandOption.Type.STRING.getValue())
                        .required(true)
                        .build()
                ).build()
        );
        slashCommands.add(ApplicationCommandRequest.builder()
                .name("bonk")
                .description("Allows to Bonk")
        .addOption(ApplicationCommandOptionData.builder()
        .name("Target")
        .description("The Target you want to Bonk")
        .type(ApplicationCommandOption.Type.USER.getValue())
        .required(true)
        .build())
                .build());
        applicationId = builder.getRestClient().getApplicationId().block();
     /*   builder.getEventDispatcher().on(ChatInputInteractionEvent.class)
                .filter(chatInputInteractionEvent ->  Flux.fromIterable(slashCommands.entrySet())
                        .filter(slashCommand -> chatInputInteractionEvent.getCommandName() == slashCommand.getKey().name())
                        .flatMap(slashCommand -> chatInputInteractionEvent.createFollowup(slashCommand.getValue().execute(chatInputInteractionEvent))))
                .subscribe(); */
        builder.on(ChatInputInteractionEvent.class, event -> {
                    if (event.getCommandName().equals("ping")) {
                        return event.reply("Pong!");
                    }
                    if (event.getCommandName().equals("greet")){
                        String name = event.getOption("name")
                                .flatMap(ApplicationCommandInteractionOption::getValue)
                                .map(ApplicationCommandInteractionOptionValue::asString)
                                .get();

                        return event.reply()
                                .withEphemeral(true)
                                .withContent("Hello, " + name);
                    }
                    if (event.getCommandName().equals("bonk")){
                        Mono<String> handle = event.getOption("Target")
                                .flatMap(ApplicationCommandInteractionOption::getValue)
                                .map(ApplicationCommandInteractionOptionValue::asUser)
                                .get()
                                .map(User::getTag);
                        return event.reply()
                                .withEphemeral(true)
                                .withContent("Bonk! " + handle.block());
                    }
                    else return null;
                }
        ).subscribe();

        Button button1 = Button.primary("1","hi");
        Button button2 = Button.secondary("2", "Ping");



     /*   builder.getEventDispatcher().on(ConnectEvent.class)
                .subscribe(event -> getAlertChannel()
                .flatMap(textChannel -> textChannel.createMessage("Connected"))
                        .then());
        builder.getEventDispatcher().on(ReconnectEvent.class)
                .subscribe(event -> getAlertChannel()
                .flatMap(textChannel -> textChannel.createMessage("Reconnected"))
                        .then());
        builder.getEventDispatcher().on(DisconnectEvent.class)
                .subscribe(event -> ev
                .flatMap(textChannel -> textChannel.createMessage("Disconnected"))
                        .then());
        builder.getEventDispatcher().on(ReconnectStartEvent.class)
                .subscribe(event -> getAlertChannel()
                        .flatMap(textChannel -> textChannel.createMessage("Reconnection started"))
                        .then());
        builder.getEventDispatcher().on(ReconnectFailEvent.class)
                .subscribe(event -> getAlertChannel()
                        .flatMap(textChannel -> textChannel.createMessage("Reconnection failed"))
                        .then());
        builder.getEventDispatcher().on(SessionInvalidatedEvent.class)
                .subscribe(event -> getAlertChannel()
                        .flatMap(textChannel -> textChannel.createMessage("Reconnection failed"))
                        .then()); */




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

        builder.onDisconnect().block();






    }
    static void addCommands(){
        commands.put("ping", event -> event.getMessage()
                .getChannel()
                .flatMap(channel -> channel.createMessage("Pong!"))
                .then());
        commands.put("addGuildSlashCommand", event -> event
                .getGuild()
                .flatMap(guild -> addGuildSlashCommands(guild.getId(),slashCommands.stream()
                        .filter(x -> x.name().equals("bonk"))
                        .findFirst()
                        .orElseThrow()))
                .then());


    }
    static Mono<Void> addGuildSlashCommands(Snowflake guildId, ApplicationCommandRequest request){
        return builder.getRestClient()
                .getApplicationService()
                .createGuildApplicationCommand(applicationId, guildId.asLong(), request)
                .then();



    }
    static Mono<TextChannel> getAlertChannel(){
       return builder.getChannelById(alertChannel).ofType(TextChannel.class);
    }
}
