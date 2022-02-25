package io.github.hellinfernal.werewolf.core.user;

import io.github.hellinfernal.werewolf.core.player.Player;

import java.util.Collection;
import java.util.List;

public interface User {
    String name();


    void tell(final String whatTodo);


    Player requestVillagerVote(Collection<Player> potentialTargets);

    Player requestWerewolfVote(Collection<Player> potentialTargets);

    Player requestKillPotionUse(Collection<Player> keySet);

    Player requestLover(List<Player> players);

    //for the vote in the night, is usually only used if you are a werewolf

    void informAboutResultOfVillagerVote(Player killedPlayer);

    void informAboutGameEnd();

    boolean requestDecisionAboutSavingLastKilledPlayer(Player lastKilledGuy);

    void informAboutFallingInLove(Player lover);


}
