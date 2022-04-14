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
    private DiscordClient _discordClient;
    private Snowflake _channelId;

    public DiscordPrinter(TextChannel channelForAll, TextChannel channelForWerewolfes){
        _channelForAll = channelForAll;
        _channelForWerewolfes = channelForAll;
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
        _channelId = channelId;
    }

    @Override
    public void informAboutStartOfTheVillagerVote() {

    }

    @Override
    public void informAboutResultOfVillagerVote(Player killedPlayer) {
        _channelForAll.createMessage(killedPlayer.user().name() + " was killed.");

    }

    @Override
    public void informAboutThingsHappendInNight() {

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


        }

        @Override
        public String name() {
            return null;
        }

        @Override
        public void tell(String whatTodo) {

        }

        @Override
        public Player requestVillagerVote(Collection<Player> potentialTargets) {
            return null;
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
                if (event.getCustomId() == selectMenu.getCustomId()) {
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
        public Player requestLover(List<Player> players) {
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
