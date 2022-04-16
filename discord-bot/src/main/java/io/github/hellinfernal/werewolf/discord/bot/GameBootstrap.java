package io.github.hellinfernal.werewolf.discord.bot;


import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.MessageEditSpec;
import discord4j.discordjson.json.ChannelCreateRequest;
import discord4j.discordjson.json.PermissionsEditRequest;
import discord4j.rest.entity.RestChannel;
import discord4j.rest.entity.RestGuild;
import io.github.hellinfernal.werewolf.core.Game;
import io.github.hellinfernal.werewolf.core.player.Player;
import io.github.hellinfernal.werewolf.core.role.GameRole;
import io.github.hellinfernal.werewolf.core.role.SpecialRole;
import io.github.hellinfernal.werewolf.core.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GameBootstrap {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameBootstrap.class);
    private long _minimumMembers = 4;
    private long _maximumMembers = 10;
    private final Set<SpecialRole> _rolesSet = new HashSet<>();
    private boolean _initiated = false;
    private final List<Member> _members = new ArrayList<>();
    private final Instant _started = Instant.now();
    private final Button _registerButton = Button.primary(UUID.randomUUID().toString(), "Join");
    private final Button _leaveButton = Button.danger(UUID.randomUUID().toString(), "Leave");
    private final Button _configButton = Button.secondary(UUID.randomUUID().toString(), ReactionEmoji.codepoints("U+2699"));
    private final Set<String> _buttonIds = Set.of(_registerButton.getCustomId().get(), _leaveButton.getCustomId().get(), _configButton.getCustomId().get());
    private              DiscordClient _discordClient;
    private final Snowflake _channelId;
    private final RestGuild _guild;
    private int _kiUsers = 0;


    public GameBootstrap(DiscordClient discordClient, final Snowflake channelId, final Snowflake guild) {
        _discordClient = discordClient;
        _channelId = channelId;
        _guild = _discordClient.getGuildById(guild);
    }


    public Mono<MessageEditSpec> configureButtonsMono(Message message){
        return Mono.just(configureButtonsEditSpec(message));
    }
    public Mono<Message> configureButtonEdit(Message message){
        return message.edit(configureButtonsEditSpec(message));
    }


    public MessageEditSpec configureButtonsEditSpec(Message message){
        MessageEditSpec newMessage  = MessageEditSpec.builder().addComponent(configureButtonsActionRow()).build();


        return newMessage;

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

    /**
     *
     * @return the total ammount of players in this game :D
     */

    private String totalPlayers() {
        int totalPlayers = _members.size() + _kiUsers;
        return String.valueOf(totalPlayers);
    }

    public boolean hasButton(final String customId) {
        return _buttonIds.contains(customId);
    }

    public boolean hasClickedRegister(final String customId) {
        return _registerButton.getCustomId().get().equalsIgnoreCase(customId);
    }

    public boolean join(final Member member) {
         if (_members.contains(member)) {
         return false;
         }
        _members.add(member);
        System.out.println(_members.size());
        return true;
    }

    public boolean hasReachedMinimumMembers() {
        return _members.size() >= get_minimumMembers();
    }

    public boolean hasClickedLeave(final String customId) {
        return _leaveButton.getCustomId().get().equalsIgnoreCase(customId);
    }

    public boolean leave(final Member member) {
        if (!_members.contains(member)) {
            return false;
        }
        _members.remove(member);
        return true;
    }

    public boolean initiate(Snowflake guildId) {
        _initiated = true;
        if (!guildId.equals(_guild.getId())) {
            throw new IllegalStateException("guild mismatch");
        }
        final RestChannel categoryChannel = _discordClient.getChannelById(
                Snowflake.of(_guild.createChannel(ChannelCreateRequest.builder()
                                .type(4)
                                .name("Werewolf Game X")
                                .build(), null)
                        .block().id().asLong())
        );

        final RestChannel werewolfChannel = _discordClient.getChannelById(
                Snowflake.of(
                        _guild.createChannel(ChannelCreateRequest.builder()
                                        .name("Werewolf Chat")
                                        .type(0)
                                        .parentId(categoryChannel.getId().asString())
                                        .build(), null)
                                .block().id().asLong()
                )
        );

        final RestChannel villagerChannel = _discordClient.getChannelById(
                Snowflake.of(
                        _guild.createChannel(ChannelCreateRequest.builder()
                                        .name("Villager Chat")
                                        .type(0)
                                        .parentId(categoryChannel.getId().asString())
                                        .build(), null)
                                .block().id().asLong()
                )
        );


        //TODO: finish it

        DiscordPrinter discordPrinter = new DiscordPrinter(villagerChannel, werewolfChannel);
        List<User> userList = discordPrinter.getDiscordWerewolfUserList(_members);
        for (int i = _kiUsers; i != 0; i--) {
            userList.add(new KiUser());
        }
        Game game = new Game(userList, List.of(discordPrinter));

        for (final Player player : game.getPlayers()) {
            if (!(player.user() instanceof DiscordPrinter.DiscordWerewolfUser)) {
                continue;
            }

            final Snowflake memberId = ((DiscordPrinter.DiscordWerewolfUser) player.user())._member.getId();
            if (player.role() == GameRole.Werewolf) {
                werewolfChannel.editChannelPermissions(memberId, PermissionsEditRequest.builder()
                                .allow(1 << 10)
                                .type(1)
                        .deny(0)
                                .build(), null)
                        .block();
            }
            villagerChannel.editChannelPermissions(memberId, PermissionsEditRequest.builder()
                            .type(1)
                            .allow(1 << 10)
                    .deny(0)
                            .build(), null)
                    .block();

            categoryChannel.editChannelPermissions(memberId, PermissionsEditRequest.builder()
                            .type(1)
                            .allow(1 << 10)
                    .deny(0)
                            .build(), null)
                    .block();

        }
        game.gameStart();
        return true;
    }

    public Instant getStarted() {
        return _started;
    }

    public boolean hasClickedConfig(String customId) {
        return _configButton.getCustomId().get().equalsIgnoreCase(customId);
    }

    /**
     * generates a config menu, which generates options to modify the game :D
     *
     * @param buttonEvent
     * @return
     */
    public Mono<Void> configMenu(ButtonInteractionEvent buttonEvent) {
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

    /**
     * A Listender which listens to the configMenu.
     *
     * @param customID The CustomID of the Menu to get the event on.
     * @param event
     * @return the eventDispatcher, .then(numberOfPlayersMenu()) or .then(rolesOptionsMenu())
     */

    public Mono<Void> configMenuListener(String customID, final ButtonInteractionEvent event) {
        return event.getClient().on(SelectMenuInteractionEvent.class, menuEvent -> {
            if (menuEvent.getCustomId().equals(customID)) {
                if (menuEvent.getValues().contains("numberOfPlayers")) {
                    LOGGER.debug("numberOfPlayersMenu created.");
                    return menuEvent.deferReply()
                            .withEphemeral(true)
                            .then(numberOfPlayersMenu(menuEvent));
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

    /** Returns the names of all roles who are at the moment in the game :D
     *
     * @return a String with said names :D
     */

    private String getActualRolesAsString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("That are the Roles who are at the moment in the Game: \n");
        _rolesSet.forEach(specialRole -> stringBuilder.append(specialRole.name()).append("\n"));
        return stringBuilder.toString();
    }

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

    /**
     * removes a Role from the Bootstrap.
     * @param specialRole the role who should be removed :D
     */

    private void removeRole(SpecialRole specialRole) {
        _rolesSet.remove(specialRole);
        LOGGER.debug("Role was removed: " + specialRole.name());
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

    private Mono<Void> possibleRolesMenu(SelectMenuInteractionEvent menuEvent) {
        //TODO: forgot lol :D
        return menuEvent.reply().withContent("Not inplemented");
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

    public long get_minimumMembers() {
        return _minimumMembers;
    }

    public void set_minimumMembers(long minimumMembers) {
        this._minimumMembers = minimumMembers;
        LOGGER.debug("_minimumMembers is set to " + _minimumMembers);
    }

    public long get_maximumMembers() {
        return _maximumMembers;
    }

    public void set_maximumMembers(long _maximumMembers) {
        this._maximumMembers = _maximumMembers;
        LOGGER.debug("_maximumMembers is set to" + _maximumMembers);
    }

    /**
     * Adds a role to _rolesSet
     * @param role the Role who is added :D
     */
    public void addRole(SpecialRole role){
        _rolesSet.add(role);
        LOGGER.debug("Role was added: " + role.name());
    }
}
