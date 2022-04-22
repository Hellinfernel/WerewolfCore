package io.github.hellinfernal.werewolf.core.async.winningcondition;

import java.util.List;

import io.github.hellinfernal.werewolf.core.Game;
import io.github.hellinfernal.werewolf.core.async.GameAsync;
import io.github.hellinfernal.werewolf.core.player.Player;
import io.github.hellinfernal.werewolf.core.role.SpecialRole;

public class AmorLoversWinningConditionAsync implements WinningConditionAsync {
    @Override
    public boolean isSatisfied( GameAsync game) {
        List<Player> Lovers = game.getSpecialRolePlayers(SpecialRole.Lover);
        if (Lovers.size() ==2){
            if(game.getPlayers().stream()
                    .filter(player -> !player.specialRoles().contains(SpecialRole.Lover) || !player.specialRoles().contains(SpecialRole.Amor))
                    .count() == 0);
            return true;

        }

        return false;
    }
}
