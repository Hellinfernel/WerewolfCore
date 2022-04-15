package io.github.hellinfernal.werewolf.core.user;

import io.github.hellinfernal.werewolf.core.player.Player;
import io.github.hellinfernal.werewolf.core.player.PlayersInLove;

import java.util.Collection;
import java.util.List;

public interface User {
    String name();


    void tell(final String whatTodo);


    /**
     * requests a vote for a VillagerMove
     * @param potentialTargets the potential targets.
     * @return a player (from the Collection if possible)
     */
    Player requestVillagerVote(Collection<Player> potentialTargets);


    /**
     * requests a vote for a WerewolfMove
     * @param potentialTargets the potential targets.
     * @return a player (from the Collection if possible)
     */
    Player requestWerewolfVote(Collection<Player> potentialTargets);


    /**
     * requests a vote for a WitchMove1
     * @param potentialTargets the potential targets.
     * @return a player (from the Collection if possible)
     */
    Player requestKillPotionUse(Collection<Player> potentialTargets);


    /**
     * requests 2 players from Amor, which are going to fall in love. Usually only called at the very beginning of a game.
     * @param players the potential Targets.
     * @return A players in love object.
     */
    PlayersInLove requestLovers(List<Player> players);



    boolean requestDecisionAboutSavingLastKilledPlayer(Player lastKilledGuy);

    void informAboutFallingInLove(Player lover);


}
