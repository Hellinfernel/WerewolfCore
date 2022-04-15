package io.github.hellinfernal.werewolf.discord.bot;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.PrivateChannel;
import discord4j.core.object.entity.channel.TextChannel;
import io.github.hellinfernal.werewolf.core.player.Player;
import io.github.hellinfernal.werewolf.core.player.PlayersInLove;
import io.github.hellinfernal.werewolf.core.user.GlobalPrinter;
import io.github.hellinfernal.werewolf.core.user.User;
import org.jetbrains.annotations.Async;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DiscordPrinter implements GlobalPrinter {
    //TODO: ich glaub eher dass wir hier den RestChannel brauchen, musst getestet werden
    TextChannel _channelForAll;
    TextChannel _channelForWerewolfes;
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordPrinter.class);
    private DiscordClient _discordClient;

    public DiscordPrinter(TextChannel channelForAll, TextChannel channelForWerewolfes){
        _channelForAll = channelForAll;
        _channelForWerewolfes = channelForWerewolfes;
        LOGGER.debug("Printer created. \n"
                + toString());
    }

    @Override
    public String toString() {
        return "DiscordPrinter{" +
                " \n _channelForAll=" + _channelForAll.toString() +
                ", \n  _channelForWerewolfes=" + _channelForWerewolfes.toString() +
                ", \n  _discordClient=" + _discordClient +
                '}';
    }

    public DiscordPrinter.DiscordWerewolfUser getDiscordWerewolfUser(Member member){
        return new DiscordWerewolfUser(member, _channelForAll, _channelForWerewolfes);

    }
    public List<User> getDiscordWerewolfUserList(List<Member> list){
        return list.stream()
                .map(member ->new DiscordPrinter.DiscordWerewolfUser(member, _channelForAll,_channelForWerewolfes))
                .collect(Collectors.toList());
    }

    public DiscordPrinter( DiscordClient discordClient, Snowflake channelId ) {
        _discordClient = discordClient;
    }

    @Override
    public void informAboutStartOfTheVillagerVote() {
        _channelForAll.createMessage("*Tack* *Tack* *Tack* \n" +
                "Ok, ok, please calm down. we will make a little talk round where everyone can make their arguments and then everyone has one vote.");
    }

    @Override
    public void informAboutResultOfVillagerVote(Player killedPlayer) {
        _channelForAll.createMessage(killedPlayer.user().name() + " was killed.");

    }

    @Override
    public void informAboutThingsHappendInNight() {
        LOGGER.debug("informAboutThingsHappendInNight() needs to be inplemented.");
        //TODO: implement.
        _channelForAll.createMessage("not inplemented yet.");

    }

    @Override
    public void informAboutGameEnd() {
        _channelForAll.createMessage("The Game ended!");

    }

    @Override
    public void informAboutChangeToDayTime() {
        _channelForAll.createMessage("The sun arises...");

    }

    @Override
    public void informAboutChangeToNightTime() {
        _channelForAll.createMessage("The sun goes down...");

    }

    @Override
    public void informAboutStartOfTheHunt() {
        _channelForAll.createMessage("The Hunt beginns...");
        _channelForWerewolfes.createMessage()
                .withContent("Ok, my fellow Werewolf... who are we going to hunt?");

    }

    @Override
    public void informAboutEndOfTheHunt() {
        _channelForAll.createMessage("The Hunt ended...");

    }

    public class DiscordWerewolfUser implements User {
        Member _member;
        TextChannel _channelForAll;
        TextChannel _channelForWerewolfes;


        public DiscordWerewolfUser(Member member, TextChannel channelForAll, TextChannel channelForWerewolfes) {
            _member = member;
            _channelForAll = channelForAll;
            _channelForWerewolfes = channelForWerewolfes;
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
            List<SelectMenu.Option> victims = potentialTargets.stream()
                    .map(player -> SelectMenu.Option.of(player.user().name(),player.user().name()))
                    .collect(Collectors.toList());
            SelectMenu selectMenu = SelectMenu.of(UUID.randomUUID().toString(),victims);
            PrivateChannel privateChannel = _member.getPrivateChannel().block();
            return privateChannel.createMessage()
                    .withContent("Ok, my friend. who should die?")
                    .withComponents(ActionRow.of(selectMenu))
                    .then(requestVillagerVoteListener(privateChannel, selectMenu, potentialTargets).next())
                    .timeout(Duration.ofMinutes(5))
                    .onErrorReturn(null)
                    .block();

        }

        private Flux<Player> requestVillagerVoteListener(PrivateChannel privateChannel, SelectMenu selectMenu, Collection<Player> potentialTargets) {
            return privateChannel.getClient().on(SelectMenuInteractionEvent.class, event ->{
                if (event.getCustomId().equals(selectMenu.getCustomId())){
                    return Mono.just(potentialTargets.stream()
                    .filter(player -> player.user()
                            .name().equals(event.getValues().stream()
                                    .findFirst()
                                    .get()))
                    .findFirst().get());
                }
                return Mono.empty();
                    }).filter(player -> player != null);
        }

        @Override
        public Player requestWerewolfVote(Collection<Player> potentialTargets) {
            List<SelectMenu.Option> victims = potentialTargets.stream()
                    .map(player -> SelectMenu.Option.of(player.user().name(),player.user().name()))
                    .collect(Collectors.toList());
                    SelectMenu selectMenu = SelectMenu.of(UUID.randomUUID().toString(),victims);
                    PrivateChannel privateChannel = _member.getPrivateChannel().block();

                return privateChannel.createMessage()
                        .withContent("Ok, my fellow Werewolf... who are we going to hunt?")
                        .withComponents(ActionRow.of(selectMenu))
                        .then(requestWerewolfVoteListener(privateChannel, selectMenu, potentialTargets).next())
                        .timeout(Duration.ofMinutes(5))
                        .onErrorReturn(null)
                        .block();




        }
        public Flux<Player> requestWerewolfVoteListener(PrivateChannel privateChannel, SelectMenu selectMenu, Collection<Player> potentialTargets) {
            return privateChannel.getClient().on(SelectMenuInteractionEvent.class, event -> {
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
                    .filter(player -> player != null);
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

}
