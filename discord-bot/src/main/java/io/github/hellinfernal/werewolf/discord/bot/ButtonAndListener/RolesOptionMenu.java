package io.github.hellinfernal.werewolf.discord.bot.ButtonAndListener;

import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.SelectMenu;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class RolesOptionMenu {

    /** generates a menu which gives the options to add or remove Roles from the game
     *
     * @param menuEvent The menuEvent on which a followup is created.
     * @return a followup with a menu and .then() a Listener.
     */

    private Mono<Void> rolesOptionsMenu(SelectMenuInteractionEvent menuEvent) {
        SelectMenu selectMenu = SelectMenu.of(UUID.randomUUID().toString(),
                SelectMenu.Option.of("Add Roles", "addRoles"),
                SelectMenu.Option.of("Remove Roles", "removeRoles"),
                SelectMenu.Option.of("Show actual Roles", "showRoles"));
        return menuEvent.createFollowup()
                .withComponents(ActionRow.of(selectMenu))
                .withEphemeral(true)
                .then(rolesOptionsMenuListener(menuEvent,selectMenu));

    }

    /**
     * Creates a Eventdispatcher who creates a Menu based on the choosen option :D
     * @param oldMenuEvent the Event on which a dispatcher should be created
     * @param selectMenu the selectMenu on which the Listener listens
     * @return a message :D
     */

    private Mono<Void> rolesOptionsMenuListener(SelectMenuInteractionEvent oldMenuEvent, SelectMenu selectMenu) {
        return oldMenuEvent.getClient().on(SelectMenuInteractionEvent.class, menuEvent ->{
                    if (selectMenu.getCustomId().equals(menuEvent.getCustomId())){
                        if (menuEvent.getValues().contains("addRoles")){
                            LOGGER.debug("addRolesMenu Created");
                            return menuEvent.deferReply()
                                    .withEphemeral(true)
                                    .then(addRolesMenu(menuEvent));

                        }
                        else if (menuEvent.getValues().contains("removeRoles")){
                            LOGGER.debug("removeRolesMenu Created");
                            return menuEvent.deferReply()
                                    .withEphemeral(true)
                                    .then(removeRolesMenu(menuEvent));
                        }
                        else if (menuEvent.getValues().contains("showRoles")){
                            LOGGER.debug("actual Roles shown.");
                            return menuEvent.reply(getActualRolesAsString());
                        }

                    }
                    return Mono.empty();
                    //TODO: Add Javadoc

                }
        ).then();

    }
}
