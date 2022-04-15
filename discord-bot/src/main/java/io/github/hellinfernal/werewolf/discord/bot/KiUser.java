package io.github.hellinfernal.werewolf.discord.bot;

import io.github.hellinfernal.werewolf.core.player.Player;
import io.github.hellinfernal.werewolf.core.player.PlayersInLove;
import io.github.hellinfernal.werewolf.core.user.User;

import java.util.Collection;
import java.util.List;
import java.util.Random;

public class KiUser implements User {

    String _name;


    public KiUser(){
        _name = nameGenerator();

    }
    @Override
    public String name() {
        return _name;
    }

    @Override
    public void tell(String whatTodo) {

    }

    @Override
    public Player requestVillagerVote(Collection<Player> potentialTargets) {
        return potentialTargets.stream().unordered().findAny().get();
    }

    @Override
    public Player requestWerewolfVote(Collection<Player> potentialTargets) {
        return potentialTargets.stream().unordered().findAny().get();
    }

    @Override
    public Player requestKillPotionUse(Collection<Player> potentialTargets) {
        return potentialTargets.stream().unordered().findAny().get();
    }

    @Override
    public PlayersInLove requestLovers(List<Player> players) {
        Player player1 = players.stream().unordered().findAny().get();
        Player player2 = players.stream().unordered().filter(player -> !player.equals(player1)).findAny().get();
        PlayersInLove.InLove inLove = new PlayersInLove.InLove(player1,player2);
        return inLove;
    }

    @Override
    public boolean requestDecisionAboutSavingLastKilledPlayer(Player lastKilledGuy) {
        Random random = new Random();
        return random.nextBoolean();
    }

    @Override
    public void informAboutFallingInLove(Player lover) {

    }

    /**
     * Generates a Random name from Constants.KIUSERNAMES in combination with a discriminator.
     * @return a name + discriminator.
     */

    public static String nameGenerator(){
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        stringBuilder.append(Constants.KIUSERNAMES.stream().unordered().findFirst().get());
        stringBuilder.append("#");

        stringBuilder.append(random.nextInt(10000));
        return stringBuilder.toString();
    }
}
