package io.github.hellinfernal.werewolf.discord.bot.ButtonAndListener;

import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.SelectMenu;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NumberOfKiPlayersMenu {

    /**
     * Generates a Menu for the ammount of KiPlayers :D
     * @param menuEvent
     * @return
     */

    private Mono<Void> numberOfKiPlayersMenu(SelectMenuInteractionEvent menuEvent) {
        List<SelectMenu.Option> playerNumberOptions = new ArrayList<>();
        for (int i = 1; i < 20; i++) {
            playerNumberOptions.add(SelectMenu.Option.of(String.valueOf(i),String.valueOf(i)));
        }
        SelectMenu selectMenu = SelectMenu.of(UUID.randomUUID().toString(), playerNumberOptions);
        return menuEvent.createFollowup()
                .withEphemeral(true)
                .withComponents(ActionRow.of(selectMenu))
                .then(numberOfKiPlayersMenuListener(menuEvent,selectMenu));

    }
}
