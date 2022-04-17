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
import discord4j.core.object.entity.channel.PrivateChannel;
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
    public class DiscordWerewolfUser implements User {
        Member _member;
        RestChannel _channelForAll;
        RestChannel _channelForWerewolfes;
        DiscordClient _discordClient;



        public DiscordWerewolfUser(Member member, RestChannel channelForAll, RestChannel channelForWerewolfes, DiscordClient discordClient) {
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
                   .blockFirst();
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
            }).next();
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
                        .then(requestWerewolfVoteListener(privateChannel, selectMenu, potentialTargets))
                        .checkpoint("WerewolfVote Checkpoint",true)
                        .block(Duration.ofMinutes(5));





        }
        public Mono<Player> requestWerewolfVoteListener(PrivateChannel privateChannel, SelectMenu selectMenu, Collection<Player> potentialTargets) {
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
                    .filter(potentialTargets::contains)
                    .next();
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
    RestChannel _channelForAll;
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordPrinter.class);
    private DiscordClient _discordClient;
    private GatewayDiscordClient _gatewayDiscordClient;
    RestChannel _channelForWerewolfes;

    @Override
    public String toString() {
        return "DiscordPrinter{" +
                " \n _channelForAll=" + _channelForAll.toString() +
                ", \n  _channelForWerewolfes=" + _channelForWerewolfes.toString() +
                ", \n  _discordClient=" + _discordClient +
                '}';
    }

    public DiscordPrinter.DiscordWerewolfUser getDiscordWerewolfUser(Member member) {
        return new DiscordWerewolfUser(member, _channelForAll, _channelForWerewolfes,_discordClient);

    }

    public List<User> getDiscordWerewolfUserList(List<Member> list) {
        return list.stream()
                .map(member -> new DiscordPrinter.DiscordWerewolfUser(member, _channelForAll, _channelForWerewolfes,_discordClient))
                .collect(Collectors.toList());
    }

    public DiscordPrinter(DiscordClient discordClient, Snowflake channelId) {
        _discordClient = discordClient;
    }

    public DiscordPrinter(RestChannel channelForAll, RestChannel channelForWerewolfes, GatewayDiscordClient gatewayDiscordClient) {
        _channelForAll = channelForAll;
        _channelForWerewolfes = channelForWerewolfes;
        _gatewayDiscordClient = gatewayDiscordClient;
        LOGGER.debug("Printer created. \n"
                + toString());
    }

    @Override
    public void informAboutStartOfTheVillagerVote() {

        Button button = Button.primary("villagerVoteButton", "Click here To Vote :D");
        _channelForAll.createMessage(MessageCreateRequest
                .builder()
                .content("*Tack* *Tack* *Tack* \n" +
                "Ok, ok, please calm down. we will make a little talk round where everyone can make their arguments and then everyone has one vote.")
                .addComponent(ActionRow.of(button).getData())
                .build())
                .subscribe();
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
        _channelForAll.createMessage(killedPlayer.user().name() + " was killed.").block();

    }

    @Override
    public void informAboutThingsHappendInNight() {
        LOGGER.debug("informAboutThingsHappendInNight() needs to be inplemented.");
        //TODO: implement.
        _channelForAll.createMessage("not inplemented yet.").block();

    }

    @Override
    public void informAboutGameEnd() {
        _channelForAll.createMessage("The Game ended!").block();

    }

    @Override
    public void informAboutChangeToDayTime() {
        _channelForAll.createMessage("The sun arises...").block();

    }

    @Override
    public void informAboutChangeToNightTime() {
        _channelForAll.createMessage("The sun goes down...").block();

    }

    @Override
    public void informAboutStartOfTheHunt() {
        _channelForAll.createMessage("The Hunt begins...").block();
        _channelForWerewolfes.createMessage("Ok, my fellow Werewolf... who are we going to hunt?").block();

    }

    @Override
    public void informAboutEndOfTheHunt() {
        _channelForAll.createMessage("The Hunt ended...").block();

    }

}
