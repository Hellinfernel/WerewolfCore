package io.github.hellinfernal.werewolf.discord.examples.Menus;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.InteractionReplyEditSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.core.spec.MessageEditSpec;
import io.github.hellinfernal.werewolf.discord.examples.BotExample;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.awt.*;

public class MenuBuilderExample extends BotExample {
    public static void main(String[] args){
        new MenuBuilderExample().example();
    }
    private final SelectMenu selectMenu = SelectMenu.of("menu",
            SelectMenu.Option.of("Hi!", "hi"),
            SelectMenu.Option.of("Bye!", "bye"));

    private static Mono<Message> apply(SelectMenuInteractionEvent menuEvent) {
        InteractionReplyEditSpec.Builder builder = InteractionReplyEditSpec.builder();
        if (menuEvent.getCustomId().equals("hi")) {
           builder.contentOrNull("Hi!!");
        }
        if (menuEvent.getCustomId().equals("bye")) {
            builder.contentOrNull("Bye!!");

        }
        return menuEvent.editReply(builder.build());


    }

    @Override
    protected Publisher<?> execute(GatewayDiscordClient gateway) {
        return gateway.on(MessageCreateEvent.class,this::createMenuMessage);
    }

    private Publisher<?> createMenuMessage(MessageCreateEvent event){
        if (!event.getMessage().getContent().equalsIgnoreCase("!createMenu")) {
            return Mono.empty();
        }
        return event.getMessage().getChannel().flatMap(channel ->
                channel.createMessage(MessageCreateSpec
                        .builder()
                        .addComponent(ActionRow.of(selectMenu))
                        .build())
                        .then(event.getClient().on(SelectMenuInteractionEvent.class, menuEvent -> menuEvent
                                .deferEdit()
                                .then(apply(menuEvent)))
                                .then()));

    }
}
