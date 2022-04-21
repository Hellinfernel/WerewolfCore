package io.github.hellinfernal.werewolf.discord.bot.ButtonAndListener;

import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.SelectMenu;
import io.github.hellinfernal.werewolf.discord.bot.Constants;
import io.github.hellinfernal.werewolf.discord.bot.GameBootstrap;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NumberOfPlayersMenu extends Mono<Consumer<GameBootstrap>> {

    public NumberOfPlayersMenu(SelectMenuInteractionEvent menuEvent) {

    }

    /**
     * A Menu with a list of Numbers. You can choose 2 of them.
     * @param menuEvent the Event on wich a followup should be created.
     * @return a Ephemeral Followup and a Menu as Component, .then() a numberOfPlayersMenuListener on that menu.
     */
    private Mono<Void> numberOfPlayersMenu(SelectMenuInteractionEvent menuEvent) {
        List<SelectMenu.Option> playerNumberOptions = new ArrayList<>();
        for (int i = 1; i < 20; i++) {
            playerNumberOptions.add(SelectMenu.Option.of(String.valueOf(i),String.valueOf(i)));
        }
        SelectMenu selectMenu = SelectMenu.of(UUID.randomUUID().toString(), playerNumberOptions)
                .withMinValues(2)
                .withMaxValues(2)
                .withPlaceholder("Select two Values. The lower will be the minimum Value, the higher the Maximum.");
        LOGGER.debug("numberOfPlayersMenu created");
        return menuEvent.createFollowup()
                .withComponents(ActionRow.of(selectMenu))
                .withEphemeral(true)
                .then(numberOfPlayersMenuListener(menuEvent, selectMenu));


    }

    /** Creates the Listener for numberOfPlayersMenu.
     * It Collects the 2 Values who come from the newMenuEvent and sets
     * _minimumMembers and _maximumMembers based on which value is higher or lower
     *
     * @param menuEvent the menuEvent from the menu before, used to get a eventDispatcher
     * @param selectMenu the Menu on which the listener is installed.
     * @return all of that stuff, and a .then()
     */
    private Mono<Void> numberOfPlayersMenuListener(SelectMenuInteractionEvent menuEvent, SelectMenu selectMenu) {
        return menuEvent.getClient().on(SelectMenuInteractionEvent.class, newMenuEvent ->{
            if (selectMenu.getCustomId().equalsIgnoreCase(newMenuEvent.getCustomId())){
                List<String> values = newMenuEvent.getValues();
                if (values.size() == 2){
                    List<Integer> valuesAsInt = values.stream()
                            .flatMapToInt(string -> IntStream.of(Integer.parseInt(string)))
                            .sorted()
                            .boxed()
                            .collect(Collectors.toList());
                    set_minimumMembers(valuesAsInt.get(0));
                    set_maximumMembers(valuesAsInt.get(1));

                }

            }

            return newMenuEvent.deferReply()
                    .then(newMenuEvent.createFollowup()
                            .withContent("Number of Minimum Members Changed to " + get_minimumMembers() + "\n" +
                                    "Number of Maximum Members Changed to " + get_maximumMembers()));





        }).then();



    }

    @Override
    public void subscribe(CoreSubscriber<? super Consumer<GameBootstrap>> actual) {

    }
}
