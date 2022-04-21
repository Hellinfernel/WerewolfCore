package io.github.hellinfernal.werewolf.discord.bot.ButtonAndListener;

import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.SelectMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class GameBootstrapButtonsAndListener {
    Logger LOGGER = LoggerFactory.getLogger(GameBootstrapButtonsAndListener.class)


    private Mono<Void> numberOfKiPlayersMenuListener(SelectMenuInteractionEvent oldMenuEvent,SelectMenu selectMenu) {
        return oldMenuEvent.getClient().on(SelectMenuInteractionEvent.class, menuEvent ->{
                    if (menuEvent.getCustomId().equals(selectMenu.getCustomId())) {
                        String choice = menuEvent.getValues().get(0);
                        _kiUsers = Integer.parseInt(choice);
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Number of Ki-Players set to: ");
                        stringBuilder.append(choice);
                        return menuEvent.deferReply()
                                .then(menuEvent.createFollowup()
                                        .withContent(stringBuilder.toString()));

                    }
                    else return Mono.empty();
                }

        ).then();


    }
}
