package io.github.hellinfernal.werewolf.core.winningcondition;

import io.github.hellinfernal.werewolf.core.Game;
import io.github.hellinfernal.werewolf.core.player.Player;

import java.util.List;

public class VillagerWinningCondition implements WinningCondition {
    @Override
    public boolean isSatisfied(final Game game) {
        final List<Player> alivePlayers = game.getAlivePlayers();
        final long werewolfs = alivePlayers.stream().filter(p -> p.role().isWerewolf()).count();
        return werewolfs == 0;
    }
}
