package io.github.hellinfernal.werewolf.discord.bot.ButtonAndListener;

import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.SelectMenu;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.UUID;

public class RemoveRolesMenu {

    /**
     * generates a menu where roles can removed from the game
     * @param menuEvent the menuEvent on which a followup is created.
     * @return a ephemeral follow up with a menu component, .then() a removeRolesMenuListener.
     */

    private Mono<Void> removeRolesMenu(SelectMenuInteractionEvent menuEvent) {
        if (_rolesSet.size() == 0){
            return menuEvent.createFollowup()
                    .withEphemeral(true)
                    .withContent("There are no Special Roles in this game yet :D")
                    .then();
        }
        ArrayList<SelectMenu.Option> options = new ArrayList<>();
        _rolesSet.forEach(specialRole -> options.add(SelectMenu.Option.of(specialRole.name(),specialRole.name())));
        SelectMenu selectMenu = SelectMenu.of(UUID.randomUUID().toString(),options)
                .withMinValues(1)
                .withMaxValues(options.size());
        LOGGER.debug("removeRolesMenu created.");
        return menuEvent.createFollowup()
                .withEphemeral(true)
                .withComponents(ActionRow.of(selectMenu))
                .then(removeRolesMenuListener(menuEvent,selectMenu));
    }
    /**
     * generates a eventDispatcher who does listen to a removeRolesMenu
     * @param oldMenuEvent the menuEvent to get a client on which a dispatcher should be created.
     * @param selectMenu the menu on which should be reacted.
     * @return a followup with a string, .then()
     */

    private Mono<Void> removeRolesMenuListener(SelectMenuInteractionEvent oldMenuEvent, SelectMenu selectMenu) {
        return oldMenuEvent.getClient().on(SelectMenuInteractionEvent.class, menuEvent -> {
            if (selectMenu.getCustomId().equals(menuEvent.getCustomId())){
                menuEvent.getValues()
                        .forEach(string -> _rolesSet.stream()
                                .filter(role -> role.name().equals(string))
                                .findFirst()
                                .ifPresent(this::removeRole));
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("The following Roles where removed: \n");
                menuEvent.getValues().forEach(string -> stringBuilder.append(string).append("\n"));
                LOGGER.debug(stringBuilder.toString());
                return menuEvent.deferReply()
                        .then(menuEvent.createFollowup()
                                .withContent(stringBuilder.toString()));
            }
            return Mono.empty();

        }).then();
}
