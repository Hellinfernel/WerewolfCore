package io.github.hellinfernal.werewolf.discord.bot.ButtonAndListener;

import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.entity.Member;
import io.github.hellinfernal.werewolf.discord.bot.GameBootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Consumer;

public class ConfigMenu extends Mono<Consumer<GameBootstrap>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigMenu.class);
    private ButtonInteractionEvent _buttonEvent;


    public ConfigMenu(ButtonInteractionEvent buttonEvent) {
        _buttonEvent = buttonEvent;
    }

    /**
     * A Listender which listens to the configMenu.
     *
     * @param customID The CustomID of the Menu to get the event on.
     * @param event
     * @return the eventDispatcher, .then(numberOfPlayersMenu()) or .then(rolesOptionsMenu())
     */

    public Mono<Consumer<GameBootstrap>> configMenuListener(String customID, final ButtonInteractionEvent event) {
        return event.getClient().on(SelectMenuInteractionEvent.class, menuEvent -> {
            if (menuEvent.getCustomId().equals(customID)) {
                if (menuEvent.getValues().contains("numberOfPlayers")) {
                    LOGGER.debug("numberOfPlayersMenu created.");
                    return menuEvent.deferReply()
                            .withEphemeral(true)
                            .then(new NumberOfPlayersMenu(menuEvent));
                }
                if (menuEvent.getValues().contains("specialRoles")) {
                    return menuEvent.deferReply()
                            .withEphemeral(true)
                            .then(rolesOptionsMenu(menuEvent));
                }
                if (menuEvent.getValues().contains("initiate")) {
                    initiate(event.getInteraction().getGuildId().get());
                    return menuEvent.deferReply().then(event.createFollowup().withContent("initiated")).then();
                }
                if (menuEvent.getValues().contains("addKiPlayers")){
                    LOGGER.debug("numberOfKiPlayersMenu created");
                    return menuEvent.deferReply()
                            .withEphemeral(true)
                            .then(numberOfKiPlayersMenu(menuEvent));
                }

            }
            return Mono.empty();
        }).then();
    }
    private Mono<Void> getConfigReaction(ButtonInteractionEvent buttonEvent, GameBootstrap bootstrap) {
        return configMenu(buttonEvent);
    }
    /**
     * generates a config menu, which generates options to modify the game :D
     *
     * @param buttonEvent
     * @return
     */
    public Mono<Consumer<GameBootstrap>> configMenu(ButtonInteractionEvent buttonEvent) {
        SelectMenu selectMenu = SelectMenu.of(UUID.randomUUID().toString(),
                SelectMenu.Option.of("Number of Players", "numberOfPlayers"),
                SelectMenu.Option.of("Special Roles", "specialRoles"),
                SelectMenu.Option.of("Initiate", "initiate"),
                SelectMenu.Option.of("Add KiPlayers :D", "addKiPlayers"));
        return buttonEvent.deferReply()
                .withEphemeral(true)
                .then(buttonEvent.createFollowup()
                        .withComponents(ActionRow.of(selectMenu))
                        .withEphemeral(true)
                        .then(configMenuListener(selectMenu.getCustomId(), buttonEvent)));
    }



    @Override
    public void subscribe(CoreSubscriber<? super Consumer<GameBootstrap>> actual) {
        _buttonEvent.deferReply().withEphemeral(true).then(configMenu(_buttonEvent)).subscribe(actual);

    }
}
