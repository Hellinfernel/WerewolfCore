package io.github.hellinfernal.werewolf.discord.examples;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.discordjson.json.ImmutableApplicationCommandRequest;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public class SlashCommand {
    ImmutableApplicationCommandRequest _applicationCommandRequest;
    Function<ChatInputInteractionEvent, Mono<Void>> _eventFunction;
    public SlashCommand(ImmutableApplicationCommandRequest applicationCommandRequest,Function<ChatInputInteractionEvent,Mono<Void>> eventFunction){
        _applicationCommandRequest = applicationCommandRequest;
        _eventFunction = eventFunction;
    }
}
