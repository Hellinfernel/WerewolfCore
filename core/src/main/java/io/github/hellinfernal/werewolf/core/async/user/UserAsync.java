package io.github.hellinfernal.werewolf.core.async.user;

import java.util.Collection;
import java.util.List;

import io.github.hellinfernal.werewolf.core.async.player.PlayerAsync;
import io.github.hellinfernal.werewolf.core.async.player.PlayersInLoveAsync;
import io.github.hellinfernal.werewolf.core.player.Player;
import io.github.hellinfernal.werewolf.core.player.PlayersInLove;

public interface UserAsync {
    String name();


    void tell(final String whatTodo);


    /**
     * requests a vote for a VillagerMove
     * @param potentialTargets the potential targets.
     * @return a player (from the Collection if possible)
     */
    PlayerAsync requestVillagerVote(Collection<PlayerAsync> potentialTargets);


    /**
     * requests a vote for a WerewolfMove
     * @param potentialTargets the potential targets.
     * @return a player (from the Collection if possible)
     */
    PlayerAsync requestWerewolfVote(Collection<PlayerAsync> potentialTargets);


    /**
     * requests a vote for a WitchMove1
     * @param potentialTargets the potential targets.
     * @return a player (from the Collection if possible)
     */
    PlayerAsync requestKillPotionUse(Collection<PlayerAsync> potentialTargets);


    /**
     * requests 2 players from Amor, which are going to fall in love. Usually only called at the very beginning of a game.
     * @param players the potential Targets.
     * @return A players in love object.
     */
    PlayersInLoveAsync requestLovers(List<PlayerAsync> players);



    boolean requestDecisionAboutSavingLastKilledPlayer(PlayerAsync lastKilledGuy);

    void informAboutFallingInLove( PlayerAsync lover);


}
