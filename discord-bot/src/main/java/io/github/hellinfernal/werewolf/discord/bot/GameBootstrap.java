package io.github.hellinfernal.werewolf.discord.bot;


import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import discord4j.core.object.PermissionOverwrite;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.CategorizableChannel;
import discord4j.core.object.entity.channel.Category;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.MessageEditSpec;
import discord4j.core.spec.TextChannelCreateSpec;
import discord4j.discordjson.json.ChannelCreateRequest;
import discord4j.discordjson.json.ChannelData;
import discord4j.discordjson.json.PermissionsEditRequest;
import discord4j.rest.entity.RestChannel;
import discord4j.rest.entity.RestGuild;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;
import io.github.hellinfernal.werewolf.core.Game;
import io.github.hellinfernal.werewolf.core.player.Player;
import io.github.hellinfernal.werewolf.core.role.GameRole;
import io.github.hellinfernal.werewolf.core.role.SpecialRole;
import io.github.hellinfernal.werewolf.core.user.User;
import io.github.hellinfernal.werewolf.core.vote.VoteMachineFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
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
    private              DiscordClient _discordClient;
    private final Snowflake _channelId;
    private final Mono<Guild> _guild;
    private int _kiUsers = 0;
    private Snowflake _category;
    private GatewayDiscordClient _gatewayDiscordClient;


    public GameBootstrap(DiscordClient discordClient, final Snowflake channelId, final Snowflake guild, GatewayDiscordClient gatewayDiscordClient) {
        _discordClient = discordClient;
        _channelId = channelId;
        _guild = gatewayDiscordClient.getGuildById(guild);
        _gatewayDiscordClient = gatewayDiscordClient;
        _category = searchForGameChannel();
    }

    private Snowflake searchForGameChannel() {
        return _guild.flatMapMany(guild -> guild.getChannels())
                .ofType(Category.class)
                .filter(category -> category.getName().equals("Werewolf Game X"))
                .map(category -> category.getId())
                .next()
                .block(Duration.ofMinutes(1));
    }


    public Mono<MessageEditSpec> configureButtonsMono(Message message){
        return Mono.just(configureButtonsEditSpec(message));
    }
    public Mono<Message> configureButtonEdit(Message message){
        return message.edit(configureButtonsEditSpec(message));
    }







    /**
     *
     * @return the total ammount of players in this game :D
     */

    private String totalPlayers() {
        int totalPlayers = _members.size() + _kiUsers;
        return String.valueOf(totalPlayers);
    }



    public Mono<Boolean> join(final Member member) {
         if (_members.contains(member)) {
         return Mono.just(false);
         }
        _members.add(member);
         LOGGER.debug("_members.size: " + _members.size());
        return Mono.just(true);
    }

    public boolean hasReachedMinimumMembers() {
        return _members.size() >= get_minimumMembers();
    }



    public Mono<Boolean> leave(final Member member) {
        if (!_members.contains(member)) {
            return Mono.just(false);
        }
        _members.remove(member);
        return Mono.just(true);
    }

    public boolean initiate(Snowflake guildId) {
        _initiated = true;
        if (!_guild.map(guild -> guild.getId().equals(guildId)).block(Duration.ofMinutes(1))) {
            throw new IllegalStateException("guild mismatch");
        }
        if (_category == null){
            _category = _guild.flatMap(channel -> channel.createCategory("Werewolf Game X"))
                    .map(category -> category.getId())
                    .block(Duration.ofMinutes(5));
                 /**   .then(_guild.flatMapMany(Guild::getChannels)
                            .ofType(Category.class)
                            .filter(category -> category.getName().equals("Werewolf Game X"))
                            .next())
                    .map(category -> category.getId())
                    .retry()
                    .emp
                    .checkpoint("New Category created: Werewolf Game X")
                    .block(Duration.ofMinutes(1));  **/

        }
        Flux<TextChannel> channelsInCategoryChannel = _gatewayDiscordClient.getChannelById(_category)
                .ofType(Category.class)
                .flatMapMany(Category::getChannels)
                .ofType(TextChannel.class);
        final Snowflake werewolfChannel = _gatewayDiscordClient.getChannelById(_category).ofType(Category.class)
                .flatMap(category ->  channelsInCategoryChannel
                .filter(channel -> channel.getName().equals("Werewolf Chat"))
                .next()
                .switchIfEmpty(
                        _guild.flatMap(guild ->
                                guild.createTextChannel(
                                        TextChannelCreateSpec.builder()
                                                .name("Werewolf Chat")
                                                .parentId(category.getId())
                                                .build()
                                )
                        )
                )

        ).retry()
                .map(textChannel -> textChannel.getId())
                .checkpoint("Get Channel: Werewolf Chat")
                .block(Duration.ofMinutes(1));





        final Snowflake villagerChannel = _gatewayDiscordClient.getChannelById(_category).ofType(Category.class)
                .flatMap(category -> channelsInCategoryChannel
                .filter(channel -> category.getName().equals("Villager Chat"))
                .next()
                .switchIfEmpty(
                        _guild.flatMap(guild ->
                                guild.createTextChannel(
                                        TextChannelCreateSpec.builder()
                                                .name("Villager Chat")
                                                .parentId(category.getId())
                                                .build()
                                )
                        )
                )
        ).retry()
                .map(textChannel -> textChannel.getId())
                .checkpoint("Get Channel: Villager Chat")
                .block(Duration.ofMinutes(1));

        //TODO: finish it

        Snowflake debugChannel = _gatewayDiscordClient.getChannelById(_category).ofType(Category.class)
                .flatMap(category -> channelsInCategoryChannel
                        .filter(channel -> category.getName().equals("Debug channel"))
                        .next()
                        .switchIfEmpty(
                                _guild.flatMap(guild ->
                                        guild.createTextChannel(
                                                TextChannelCreateSpec.builder()
                                                        .name("Debug Channel")
                                                        .permissionOverwrites(guild.getRoles().filter(role -> role.getPosition().block(Duration.ofMinutes(5)) < 4)
                                                                .map(role -> PermissionOverwrite.forRole(role.getId(),PermissionSet.none(),PermissionSet.all()))
                                                                .collectList().block(Duration.ofMinutes(5)))
                                                        .parentId(category.getId())
                                                        .build()
                                        )
                                )
                        )
                ).retry()
                .map(textChannel -> textChannel.getId())
                .checkpoint("Get Channel: Debug")
                .block(Duration.ofMinutes(5));
        DiscordPrinter discordPrinter = new DiscordPrinter(villagerChannel, werewolfChannel, debugChannel,_gatewayDiscordClient);
        List<User> userList = discordPrinter.getDiscordWerewolfUserList(_members);
        for (int i = _kiUsers; i != 0; i--) {
            userList.add(new KiUser());
        }
        VoteMachineFactory voteStrategy = new VoteMachineFactory(VoteMachineFactory.Machines.IMPERATIV_MACHINE);
        Game game = new Game(userList, List.of(discordPrinter), voteStrategy);

        Flux.fromIterable(game.getPlayers())
                .filter(player -> player.role().isWerewolf())
                .map(Player::user)
                .ofType(DiscordPrinter.DiscordWerewolfUser.class)
                .flatMap(user ->
                        _gatewayDiscordClient.getChannelById(werewolfChannel)
                                .ofType(TextChannel.class)
                                .flatMap(
                                        hereWerewolfChannel -> hereWerewolfChannel.addMemberOverwrite(
                                        user._member.getId(),
                                        PermissionOverwrite.forMember(
                                                user._member.getId(),
                                                PermissionSet.of(Permission.SEND_MESSAGES,Permission.VIEW_CHANNEL,Permission.ADD_REACTIONS),
                                                PermissionSet.none()
                                        )

                                )
                        )

                )
                .checkpoint("Give Werewolfes Permissions")
                .subscribe();
        Flux.fromIterable(game.getPlayers())
                .map(Player::user)
                .ofType(DiscordPrinter.DiscordWerewolfUser.class)
                .flatMap(user ->
                        _gatewayDiscordClient.getChannelById(villagerChannel).ofType(TextChannel.class).flatMap(
                                hereVillagerChannel ->
                                        hereVillagerChannel
                                                .addMemberOverwrite(
                                                user._member.getId(),
                                                PermissionOverwrite.forMember(
                                                        user._member.getId(),
                                                        PermissionSet.of(Permission.SEND_MESSAGES,Permission.VIEW_CHANNEL,Permission.ADD_REACTIONS),
                                                        PermissionSet.none()
                                                )
                                )
                        )
                )
                .checkpoint("Give Villagers Permissions")
                .subscribe();



      /**  for (final Player player : game.getPlayers()) {
            if (!(player.user() instanceof DiscordPrinter.DiscordWerewolfUser)) {
                continue;
            }

            final Snowflake memberId = ((DiscordPrinter.DiscordWerewolfUser) player.user())._member.getId();
            if (player.role() == GameRole.Werewolf) {
                werewolfChannel.map(textChannel -> textChannel.addMemberOverwrite(player.user().))
                /** werewolfChannel.editChannelPermissions(memberId, PermissionsEditRequest.builder()
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

        } **/
        game.gameStart();
        return true;
    }

    public Instant getStarted() {
        return _started;
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




    }

    /**
     * removes a Role from the Bootstrap.
     * @param specialRole the role who should be removed :D
     */

    private void removeRole(SpecialRole specialRole) {
        _rolesSet.remove(specialRole);
        LOGGER.debug("Role was removed: " + specialRole.name());
    }






    private Mono<Void> possibleRolesMenu(SelectMenuInteractionEvent menuEvent) {
        //TODO: forgot lol :D
        return menuEvent.reply().withContent("Not inplemented");
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
