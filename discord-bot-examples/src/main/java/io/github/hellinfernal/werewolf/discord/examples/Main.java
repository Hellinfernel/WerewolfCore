package io.github.hellinfernal.werewolf.discord.examples;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import discord4j.core.event.domain.lifecycle.*;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.discordjson.json.ImmutableApplicationCommandRequest;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.awt.*;
import java.lang.annotation.Target;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {
    static GatewayDiscordClient builder;
    private static final Map<String, Command> commands = new HashMap<>();
    public static final Snowflake alertChannel = Snowflake.of(947919993255895132L);
   // public static final Map<ApplicationCommandRequest, SlashCommand> slashCommands = new ArrayList<>();

    static Map<ImmutableApplicationCommandRequest, Function<ChatInputInteractionEvent, Mono<Void>>> newSlashCommands = new HashMap<>();
    static Map<String, Function<ChatInputInteractionEvent,Mono<Void>>> globalSlashCommands = new HashMap<>();
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





        applicationId = builder.getRestClient().getApplicationId().block();
        globalSlashCommands.put("ping", event -> event.reply("Pong!"));
        try {
            new GlobalCommandRegistrar(builder.getRestClient()).registerCommands(new ArrayList<>());

        }catch (Exception e){
            System.out.println("Shit");

        }
     /*   builder.getEventDispatcher().on(ChatInputInteractionEvent.class)
                .filter(chatInputInteractionEvent ->  Flux.fromIterable(slashCommands.entrySet())
                        .filter(slashCommand -> chatInputInteractionEvent.getCommandName() == slashCommand.getKey().name())
                        .flatMap(slashCommand -> chatInputInteractionEvent.createFollowup(slashCommand.getValue().execute(chatInputInteractionEvent))))
                .subscribe(); */
        Button button1 = Button.primary("1","hi");
        Button button2 = Button.secondary("2", "Ping");
        SelectMenu selectMenu = SelectMenu.of("Menu",
                SelectMenu.Option.of("Ping","ping")
                        .withDescription("Ping. " +
                                "just Ping."),
                SelectMenu.Option.of("Greet", "greet"))
                .withPlaceholder("pls choose something :D");
        builder.on(ChatInputInteractionEvent.class, event -> Flux.fromIterable(newSlashCommands.entrySet())
                .filter(slashCommand -> slashCommand.getKey().name().equals(event.getCommandName()))
                .flatMap(slashCommand -> slashCommand.getValue().apply(event))
                .next())
                .subscribe();
        newSlashCommands.put(ApplicationCommandRequest.builder()
                .name("ping")
                .description("Makes a Pong")
                .build(), event -> event.reply("Pong!"));
        newSlashCommands.put(ApplicationCommandRequest.builder()
                .name("greet")
                .description("Greets you")
                .addOption(ApplicationCommandOptionData.builder()
                        .name("name")
                        .description("Your name")
                        .type(ApplicationCommandOption.Type.STRING.getValue())
                        .required(true)
                        .build()
                ).build(),
                event -> {
            String name = event.getOption("name")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get();
        return event.reply()
                .withEphemeral(true)
                .withContent("Hello, " + name);});
        newSlashCommands.put(ApplicationCommandRequest.builder()
                .name("bonk")
                .description("Allows to Bonk")
                .addOption(ApplicationCommandOptionData.builder()
                        .name("target")
                        .description("The Target you want to Bonk")
                        .type(ApplicationCommandOption.Type.USER.getValue())
                        .required(true)
                        .build())
                .build(),event ->{ Mono<String> handle = event.getOption("target")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asUser)
                .get()
                .map(User::getTag);
        return event.reply()
                .withEphemeral(false)
                .withContent("Bonk! " + handle.block());});
        newSlashCommands.put(ApplicationCommandRequest.builder()
                .name("buttoncommand")
                .description("generates a button")
                .build(),
                event -> event.reply()
                .withComponents(ActionRow.of(button1),ActionRow.of(button2)));
        newSlashCommands.put(ApplicationCommandRequest.builder()
                .name("menucommand")
                .description("generates a menu")
                .build(),event -> event.reply().withComponents(ActionRow.of(selectMenu)));
        newSlashCommands.put(ApplicationCommandRequest.builder()
                .name("timeoutbutton")
        .description("generates a temporary button")
        .build(), event -> {
           Button button = Button.primary("timebutton", "Hey!");
        return event.reply()
                .withComponents(ActionRow.of(button))
                .then(builder.on(ButtonInteractionEvent.class, buttonEvent ->{
                    if (buttonEvent.getCustomId().equals("timebutton")){
                        return buttonEvent.reply("You clicked me!").withEphemeral(true);
                    } else {
                        return Mono.empty();
                    }

                })
                        .timeout(Duration.ofMinutes(30))
                        //.onErrorResume(TimeoutException.class, ignore -> button = button.disabled())
                        .then());});


        builder.on(ButtonInteractionEvent.class, event -> {

                    if(event.getCustomId().equals("2")){
                        return event.reply("Pong!");

                    }
                    if (event.getCustomId().equals("1")){
                        return event.reply("hi");
                    }
                    else return null;
        }
                ).subscribe();
        builder.on(SelectMenuInteractionEvent.class, event ->{

            if (event.getValues().contains("ping")){
                return event.reply("Pong!");
            }
            if (event.getValues().contains("greet")){


                return event.reply()
                        .withEphemeral(true)
                        .withContent("Hello");
            }
            else return null;

        } ).subscribe();




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
                .flatMap(guild -> addGuildSlashCommands(guild.getId(),newSlashCommands.entrySet().stream()
                        .filter(x -> x.getKey().name().equals("timeoutbutton"))
                        .findFirst()
                        .orElseThrow()
                        .getKey()))
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
