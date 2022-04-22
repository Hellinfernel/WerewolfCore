package io.github.hellinfernal.werewolf.core.async.winningcondition;

import java.util.List;

import io.github.hellinfernal.werewolf.core.Game;
import io.github.hellinfernal.werewolf.core.async.GameAsync;
import io.github.hellinfernal.werewolf.core.player.Player;

public class WerewolfWinningConditionAsync implements WinningConditionAsync {
    @Override
    public boolean isSatisfied(final GameAsync game) {
        final List<Player> alivePlayers = game.getAlivePlayers();
        final long werewolfs = alivePlayers.stream().filter(p -> p.role().isWerewolf()).count();
        final long villagers = alivePlayers.stream().filter(p -> p.role().isVillager()).count();
        return werewolfs >= villagers;
    }
}
