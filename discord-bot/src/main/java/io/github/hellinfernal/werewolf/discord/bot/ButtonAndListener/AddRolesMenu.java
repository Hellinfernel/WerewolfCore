package io.github.hellinfernal.werewolf.discord.bot.ButtonAndListener;

import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.SelectMenu;
import io.github.hellinfernal.werewolf.discord.bot.Constants;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.UUID;

public class AddRolesMenu {

    /**
     * Creates a Listener who listens to a AddRolesMenu and reacts on it.
     * @param oldMenuEvent the menuEvent on which client a event should be created.
     * @param selectMenu the selectMenu on which should be reacted.
     * @return a followup with a string, .then()
     */

    private Mono<Void> addRolesMenuListener(SelectMenuInteractionEvent oldMenuEvent, SelectMenu selectMenu) {
        return oldMenuEvent.getClient().on(SelectMenuInteractionEvent.class, menuEvent -> {
            if (selectMenu.getCustomId().equals(menuEvent.getCustomId())){
                menuEvent.getValues()
                        .forEach(string -> Constants.ALL_SPECIALROLES.stream()
                                .filter(role -> role.name().equals(string))
                                .findFirst()
                                .ifPresent(this::addRole));
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("The following Roles where added: \n");
                menuEvent.getValues().forEach(string -> stringBuilder.append(string).append("\n"));
                LOGGER.debug(stringBuilder.toString());
                return menuEvent.deferReply()
                        .then(menuEvent.createFollowup()
                                .withContent(stringBuilder.toString()));
            }
            return Mono.empty();

        }).then();

    }
    /**
     * Creates a Menu where roles can be choosen who will be added to the game.
     * @param menuEvent the menuEvent where a followup is created
     * @return a ephemeral followup with a menu, .then() a Listener
     */

    private Mono<Void> addRolesMenu(SelectMenuInteractionEvent menuEvent) {
        ArrayList<SelectMenu.Option> options = new ArrayList<>();
        Constants.ALL_SPECIALROLES.stream()
                .filter(o -> !_rolesSet.contains(o))
                .forEach(specialRole -> options.add(SelectMenu.Option.of(specialRole.name(),specialRole.name())));
        if (options.size() == 0){
            return menuEvent.createFollowup()
                    .withEphemeral(true)
                    .withContent("There is no role that could be added :D")
                    .then();
        }
        SelectMenu selectMenu = SelectMenu.of(UUID.randomUUID().toString(),options)
                .withMinValues(1)
                .withMaxValues(options.size());
        LOGGER.debug("addRolesMenu created.");
        return menuEvent.createFollowup()
                .withEphemeral(true)
                .withComponents(ActionRow.of(selectMenu))
                .then(addRolesMenuListener(menuEvent,selectMenu));

    }
}
