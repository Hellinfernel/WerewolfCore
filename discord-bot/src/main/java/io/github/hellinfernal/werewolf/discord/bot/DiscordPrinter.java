package io.github.hellinfernal.werewolf.discord.bot;

import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.TextChannel;
import io.github.hellinfernal.werewolf.core.player.Player;
import io.github.hellinfernal.werewolf.core.user.GlobalPrinter;

public class DiscordPrinter implements GlobalPrinter {
    TextChannel _channelForAll;
    TextChannel _channelForWerewolfes;

    public DiscordPrinter(TextChannel channelForAll){
        _channelForAll = channelForAll;
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

    }

    @Override
    public void informAboutEndOfTheHunt() {
        _channelForAll.createMessage("The Hunt ended...");

    }
}
