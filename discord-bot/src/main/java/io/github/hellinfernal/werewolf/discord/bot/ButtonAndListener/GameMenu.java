package io.github.hellinfernal.werewolf.discord.bot.ButtonAndListener;

import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.InteractionFollowupCreateMono;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.core.spec.MessageEditSpec;
import discord4j.discordjson.json.MessageCreateRequest;
import io.github.hellinfernal.werewolf.core.Game;
import io.github.hellinfernal.werewolf.discord.bot.GameBootstrap;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GameMenu extends Flux<Consumer<GameBootstrap>> {
    final MessageCreateEvent _event;
    private final Button _registerButton = Button.primary(UUID.randomUUID().toString(), "Join");
    private final Button _leaveButton = Button.danger(UUID.randomUUID().toString(), "Leave");
    private final Button _configButton = Button.secondary(UUID.randomUUID().toString(), ReactionEmoji.codepoints("U+2699"));
    private final Set<String> _buttonIds = Set.of(_registerButton.getCustomId().get(), _leaveButton.getCustomId().get(), _configButton.getCustomId().get());

    public GameMenu(MessageCreateEvent event){
        _event = event;

    }

    /**
     * Gets a EventDispatcher on ButtonInteractionEvent to Listen to the Buttons. The idea is that every button
     * will in the end return a consumer which is supposed to be pushed to GameMenus Subscriber
     * @return
     */
    private Flux<Consumer<GameBootstrap>> generateGameMenu() {







        return _event.getClient().on(ButtonInteractionEvent.class, buttonEvent -> {
           /** final GameBootstrap bootstrap = _gamesToBootstrapByChannel
                    .values()
                    .stream()
                    .filter(v -> v.hasButton(buttonEvent.getCustomId()))
                    .findFirst()
                    .orElse(null); **/
            //TODO: remove comment, this makes problems in the actual game

            /** if (bootstrap == null) {
             return buttonEvent
             .deferReply()
             .withEphemeral(true)
             .then(buttonEvent.createFollowup("Sorry, the game you tried to interact with, already started.")
             .withEphemeral(true))
             .then();} **/

                if (hasClickedRegister(buttonEvent.getCustomId()))
                    return getJoinReaction(buttonEvent);
                if (hasClickedLeave(buttonEvent.getCustomId()))
                    return getLeaveReaction(buttonEvent);
                if (hasClickedConfig(buttonEvent.getCustomId())) {
                    return new ConfigMenu(buttonEvent);
                }
                return Mono.empty();




        });
    }
    @org.jetbrains.annotations.NotNull
    private Mono<Consumer<GameBootstrap>> getJoinReaction(ButtonInteractionEvent buttonEvent) {
        final Member member = buttonEvent.getInteraction().getMember().get();
        return Mono.just(gameBootsTrap -> gameBootsTrap.join(member).flatMap(bool -> joinMessage(bool,buttonEvent,member)));
        /** if (bootstrap.join(member)) {
             if (bootstrap.hasReachedMinimumMembers()) {
             bootstrap.initiate(menuEvent);
             gameStarted = () -> buttonEvent
             .getMessage()
             .map(bootstrap::configureButtonEdit)
             .get()
             .then(buttonEvent.createFollowup("Game has started, have fun!"));
             }  //TODO: remove comment :D
            return buttonEvent
                    .deferReply()
                    .then(buttonEvent
                            .getMessage()
                            .map(bootstrap::configureButtonEdit)
                            .get())
                    .then(buttonEvent.createFollowup(member
                            .getTag() + " was added"))
                    .then(gameStarted.get())
                    .then();
        } else {
            return buttonEvent
                    .deferReply()
                    .withEphemeral(true)
                    .then(buttonEvent.createFollowup("You are already in the game :D")
                            .withEphemeral(true))
                    .then();


        } **/
    }

    @org.jetbrains.annotations.NotNull
    private Mono<Consumer<GameBootstrap>> getLeaveReaction(ButtonInteractionEvent buttonEvent) {
        final Member member = buttonEvent.getInteraction().getMember().get();
        return Mono.just(gameBootstrap -> gameBootstrap.leave(member).flatMap(bool -> leaveMessage(bool,buttonEvent,member)));
       /** if (bootstrap.leave(member)) {
            return buttonEvent
                    .deferReply()
                    .then(Mono.just(bootstrap.configureButtonsEditSpec(buttonEvent.getMessage().get())))
                    .then(buttonEvent.createFollowup(member
                            .getTag() + " was removed"))
                    .then();
        } else {
            return buttonEvent
                    .deferReply()
                    .withEphemeral(true)
                    .then(buttonEvent.createFollowup("You aren't in the game :D")
                            .withEphemeral(true))
                    .then();
        } **/
    }


    private InteractionFollowupCreateMono joinMessage(Boolean bool, ButtonInteractionEvent event, Member member) {
        if (bool = true){
            return event.createFollowup().withContent(member.getTag() + " was added to the Game.");
        }
        else {
            return event.createFollowup().withEphemeral(true).withContent("You are already in the game :D");

        }
    }
    private InteractionFollowupCreateMono leaveMessage(Boolean bool, ButtonInteractionEvent event, Member member){
        if (bool = true){
            return event.createFollowup().withContent(member.getTag() + " has left the Game.");
        }
        else {
            return event.createFollowup().withEphemeral(true).withContent("You arent it the game yet :D");
        }
    }
    public boolean hasButton(final String customId) {
        return _buttonIds.contains(customId);
    }

    public boolean hasClickedRegister(final String customId) {
        return _registerButton.getCustomId().orElse("fart").equalsIgnoreCase(customId);
    }
    public boolean hasClickedConfig(String customId) {
        return _configButton.getCustomId().get().equalsIgnoreCase(customId);
    }
    public boolean hasClickedLeave(final String customId) {
        return _leaveButton.getCustomId().orElse("fart").equalsIgnoreCase(customId);
    }

    public ActionRow configureButtonsActionRow(){
        final Button registerButton = getRegisterButton();
        final Button leaveButton = getLeaveButton();
        final Button configButton = getConfigButton();
        return ActionRow.of(registerButton, leaveButton, configButton);

    }

    private Button getConfigButton() {
        Button configButton = _configButton;
        if (_initiated){
            configButton = configButton.disabled();
        }
        return configButton;
    }

    private Button getLeaveButton() {
        Button leaveButton = _leaveButton;
        if (_initiated){
            leaveButton = leaveButton.disabled();
        }
        return leaveButton;
    }

    private Button getRegisterButton() {
        Button registerButton = _registerButton;

        registerButton = Button.primary(registerButton.getCustomId().get(), "Join (" + totalPlayers() + ")");
        if (_initiated){
            registerButton = registerButton.disabled();
        }

        return registerButton;
    }

    public MessageEditSpec configureButtonsEditSpec(Message message){
        MessageEditSpec newMessage  = MessageEditSpec.builder().addComponent(configureButtonsActionRow()).build();


        return newMessage;

    }





    @Override
    public void subscribe(CoreSubscriber<? super Consumer<GameBootstrap>> actual) {
        _event.getMessage().getChannel().flatMap(channel -> channel.createMessage(MessageCreateSpec.builder()
                .content("Please click Join to join the game, game will start within the next 3 minutes.")
                .addComponent(configureButtonsActionRow())
                .build()))
                .thenMany(generateGameMenu())
                .subscribe(actual);
                /**.timeout(Duration.ofMinutes(3))
                .onErrorResume(TimeoutException.class, ignore -> {
                    //TODO: later
                    //channelGame.initiate();
                    return Mono.empty();
                })**/


    }


}
