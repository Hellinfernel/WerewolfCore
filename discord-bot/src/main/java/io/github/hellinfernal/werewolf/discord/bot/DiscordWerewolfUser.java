package io.github.hellinfernal.werewolf.discord.bot;

import discord4j.core.object.entity.Member;
import io.github.hellinfernal.werewolf.core.player.Player;
import io.github.hellinfernal.werewolf.core.player.PlayersInLove;
import io.github.hellinfernal.werewolf.core.user.User;

import java.util.Collection;
import java.util.List;

public class DiscordWerewolfUser implements User {
    Member _member;
    public DiscordWerewolfUser(Member member) {
        _member = member;
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
        return null;
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
