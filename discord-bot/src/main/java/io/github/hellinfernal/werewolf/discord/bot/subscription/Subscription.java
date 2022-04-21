package io.github.hellinfernal.werewolf.discord.bot.subscription;

import discord4j.core.event.domain.Event;
import reactor.core.publisher.Mono;


public interface Subscription<T extends Event> {
   Mono<Void> handle(final T event);
}
