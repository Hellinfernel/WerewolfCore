package io.github.hellinfernal.werewolf.discord.bot;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.PrivateChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.discordjson.json.MessageCreateRequest;
import discord4j.rest.entity.RestChannel;
import io.github.hellinfernal.werewolf.core.player.Player;
import io.github.hellinfernal.werewolf.core.player.PlayersInLove;
import io.github.hellinfernal.werewolf.core.user.GlobalPrinter;
import io.github.hellinfernal.werewolf.core.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class DiscordPrinter implements GlobalPrinter {
    private final Snowflake _channelForDebug;

    public class DiscordWerewolfUser implements User {
        Member _member;
        Snowflake _channelForAll;
        Snowflake _channelForWerewolfes;
        DiscordClient _discordClient;



        public DiscordWerewolfUser(Member member, Snowflake channelForAll, Snowflake channelForWerewolfes, DiscordClient discordClient) {
            _member = member;
            _channelForAll = channelForAll;
            _channelForWerewolfes = channelForWerewolfes;
            _discordClient = discordClient;
            LOGGER.debug("DiscordWerewolfUser created: " + member.getTag());



        }

        @Override
        public String name() {
            return _member.getTag();
        }

        @Override
        public void tell(String whatTodo) {

        }

        @Override
        public Player requestVillagerVote(Collection<Player> potentialTargets) {
           return _gatewayDiscordClient.on(ButtonInteractionEvent.class,buttonEvent -> {
                        if (buttonEvent.getCustomId().equals("villagerVoteButton")) {
                            List<SelectMenu.Option> victims = potentialTargets.stream()
                                    .map(player -> SelectMenu.Option.of(player.user().name(), player.user().name()))
                                    .collect(Collectors.toList());
                            SelectMenu selectMenu = SelectMenu.of(UUID.randomUUID().toString(), victims);
                            return buttonEvent.deferReply()
                                    .withEphemeral(true)
                                    .then(buttonEvent.createFollowup()
                                            .withEphemeral(true)
                                            .withContent("This is your Voting Menu. Choose one of them, ")
                                            .withComponents(ActionRow.of(selectMenu)))
                                    .then(requestVillagerVoteListener(selectMenu, potentialTargets));
                        }
                        return Mono.empty();
                    })
                   .checkpoint("VillagerVote requested from: " + _member.getUsername())
                   .blockFirst(Duration.ofMinutes(5));
        }

        private Mono<Player> requestVillagerVoteListener(SelectMenu selectMenu, Collection<Player> potentialTargets) {

            return _gatewayDiscordClient.on(SelectMenuInteractionEvent.class, event -> {
                if (event.getCustomId().equals(selectMenu.getCustomId())) {
                    return Mono.just(potentialTargets.stream()
                            .filter(player -> player.user()
                                    .name().equals(event.getValues().stream()
                                            .findFirst()
                                            .get()))
                            .findFirst().get());
                }
                return Mono.empty();
            }).checkpoint("requestVillagerVoteListener created for Player: " + _member.getUsername())
                    .next();
        }

        @Override
        public Player requestWerewolfVote(Collection<Player> potentialTargets) {



                return werewolfButtonListener(potentialTargets).block(Duration.ofMinutes(5));





        }
        public Mono<Player> werewolfButtonListener(Collection<Player> potentialTargets){
            return _gatewayDiscordClient.on(ButtonInteractionEvent.class, event -> {
                if (event.getCustomId().equals("werewolfVoteButton")){
                    if (event.getInteraction().getMember().get().equals(_member) ){
                        List<SelectMenu.Option> victims = potentialTargets.stream()
                                .map(player -> SelectMenu.Option.of(player.user().name(),player.user().name()))
                                .collect(Collectors.toList());
                        SelectMenu selectMenu = SelectMenu.of(UUID.randomUUID().toString(),victims);
                        return event.createFollowup()
                                .withEphemeral(true)
                                .withComponents(ActionRow.of(selectMenu))
                                .then(requestWerewolfVoteListener(potentialTargets,selectMenu));

                    }

                }
                return Mono.empty();
            })
                    .checkpoint("werewolfButtonListener created for: " + _member.getUsername()).next();

        }
        public Mono<Player> requestWerewolfVoteListener(Collection<Player> potentialTargets, SelectMenu selectMenu) {
            return _gatewayDiscordClient.on(SelectMenuInteractionEvent.class, event -> {
                if (event.getCustomId().equals(selectMenu.getCustomId())) {

                        return Mono.just(potentialTargets.stream()
                                .filter(player -> player.user()
                                        .name()
                                        .equals(event.getValues().stream()
                                                .findFirst()
                                                .get()))
                                .findFirst().get());


                }
                return Mono.empty();
            })
                    .filter(potentialTargets::contains)
                    .next()
                    .checkpoint(_member.getUsername() + " choosed a player :D (Werewolf)");
        }

        @Override
        public Player requestKillPotionUse(Collection<Player> keySet) {
            return null;
        }


        @Override
        public PlayersInLove requestLovers(List<Player> players) {
            return null;
        }


        @Override
        public boolean requestDecisionAboutSavingLastKilledPlayer(Player lastKilledGuy) {
            return false;
        }

        @Override
        public void informAboutFallingInLove(Player lover) {

        }
    }

    //TODO: ich glaub eher dass wir hier den RestChannel brauchen, musst getestet werden
    //TODO: der Discord4J Server meint, dass wir auf REST verzichten sollten :D
    //TODO: also ich hab mal auf snowflakes umgestellt xD
    Snowflake _channelForAll;
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordPrinter.class);
    private DiscordClient _discordClient;
    private GatewayDiscordClient _gatewayDiscordClient;
    Snowflake _channelForWerewolfes;

    @Override
    public String toString() {
        return "DiscordPrinter{" +
                " \n _channelForAll=" + _channelForAll.asString() +
                ", \n  _channelForWerewolfes=" + _channelForWerewolfes.asString() +
                ", \n  _discordClient=" + _discordClient +
                '}';
    }

    public DiscordPrinter.DiscordWerewolfUser getDiscordWerewolfUser(Member member) {
        DiscordWerewolfUser discordWerewolfUser = new DiscordWerewolfUser(member, _channelForAll, _channelForWerewolfes, _discordClient);
        debugPrint("New user Generated: " + discordWerewolfUser.toString());
        return discordWerewolfUser;

    }

    public List<User> getDiscordWerewolfUserList(List<Member> list) {
        return list.stream()
                .map(member -> new DiscordPrinter.DiscordWerewolfUser(member, _channelForAll, _channelForWerewolfes,_discordClient))
                .collect(Collectors.toList());
    }

   // public DiscordPrinter(DiscordClient discordClient, Snowflake channelId) {
    //    _discordClient = discordClient;
   // }

    public DiscordPrinter(Snowflake channelForAll, Snowflake channelForWerewolfes, Snowflake channelforDebug, GatewayDiscordClient gatewayDiscordClient) {
        _channelForAll = channelForAll;
        _channelForWerewolfes = channelForWerewolfes;
        _gatewayDiscordClient = gatewayDiscordClient;
        _channelForDebug = channelforDebug;
        debugPrint("Printer created. \n"
                + toString());
        //_gatewayDiscordClient.getGuildById().block().getChannels()
    }

    @Override
    public void informAboutStartOfTheVillagerVote() {

        Button button = Button.primary("villagerVoteButton", "Click here To Vote :D");
        _gatewayDiscordClient.getChannelById(_channelForAll)
                .ofType(TextChannel.class)
                .flatMap(channel -> channel.createMessage()
                .withContent("*Tack* *Tack* *Tack* \n" +
                        "Ok, ok, please calm down. we will make a little talk round where everyone can make their arguments and then everyone has one vote.")
                .withComponents(ActionRow.of(button)))
                .subscribe();
        debugPrint("VillagerVote Started");
    }

  /**  private Mono<Void> startOfTheVillagerVoteListener() {
        return _gatewayDiscordClient.on(ButtonInteractionEvent.class,buttonEvent -> {
            if (buttonEvent.getCustomId().equals("villagerVoteButton")){
                return buttonEvent.deferReply()
                        .withEphemeral(true)
                        .then(buttonEvent.createFollowup()
                                .withEphemeral(true)
                                .withContent("This is your Voting Menu. Choose one of them, ")
                                .withComponents());
            }
            else return Mono.empty();
        })
                .then();

    } **/

    @Override
    public void informAboutResultOfVillagerVote(Player killedPlayer) {
        _gatewayDiscordClient.getChannelById(_channelForAll)
                .ofType(TextChannel.class)
                .flatMap(channel -> channel.createMessage(killedPlayer.user().name() + " was killed."))
                .subscribe();
        debugPrint(killedPlayer.user().name() + " was killed.");

    }

    @Override
    public void informAboutThingsHappendInNight() {
        LOGGER.debug("informAboutThingsHappendInNight() needs to be inplemented.");
        //TODO: implement.
        _gatewayDiscordClient.getChannelById(_channelForAll)
                .ofType(TextChannel.class)
        .flatMap(channel -> channel.createMessage("not inplemented yet."))
                .subscribe();
        debugPrint("informAboutThingsHappendInNight() needs to be inplemented.");

    }

    @Override
    public void informAboutGameEnd() {
        _gatewayDiscordClient.getChannelById(_channelForAll)
                .ofType(TextChannel.class)
        .flatMap(channel -> channel.createMessage("The Game ended!"))
                .checkpoint("The Game is Over :D").subscribe();
        debugPrint("the game ended");

    }

    @Override
    public void informAboutChangeToDayTime() {
        _gatewayDiscordClient.getChannelById(_channelForAll)
                .ofType(TextChannel.class)
                .flatMap(channel -> channel.createMessage("The sun arises..."))
                .subscribe();
        debugPrint("its now Day");

    }

    @Override
    public void informAboutChangeToNightTime() {
        _gatewayDiscordClient.getChannelById(_channelForAll)
                .ofType(TextChannel.class)
                .flatMap(channel -> channel.createMessage("The sun goes down..."))
                .subscribe();
        debugPrint("its now Night");

    }

    @Override
    public void informAboutStartOfTheHunt() {
        _gatewayDiscordClient.getChannelById(_channelForAll)
                .ofType(TextChannel.class)
                .flatMap(channel -> channel.createMessage("The Hunt begins..."))
                .subscribe();
        Button button = Button.danger("werewolfVoteButton","Choose your victim...");
        _gatewayDiscordClient.getChannelById(_channelForWerewolfes)
                .ofType(TextChannel.class)
                .flatMap(channel -> channel.createMessage()
                .withContent("Ok, my fellow Werewolf... who are we going to hunt?")
                .withComponents(ActionRow.of(button)))
                .subscribe();
        debugPrint("hunt Started");

    }

    @Override
    public void informAboutEndOfTheHunt() {
        _gatewayDiscordClient.getChannelById(_channelForAll)
                .ofType(TextChannel.class)
        .flatMap(channel -> channel.createMessage("The Hunt ended...")).checkpoint("End of the Hunt").subscribe();
        debugPrint("Hunt ended");

    }



    @Override
    public void debugPrint(String print){
        _gatewayDiscordClient.getChannelById(_channelForDebug)
                .ofType(TextChannel.class)
                .flatMap(channel -> channel.createMessage(print)).checkpoint(print).subscribe();
        LOGGER.debug(print);
    }

}
