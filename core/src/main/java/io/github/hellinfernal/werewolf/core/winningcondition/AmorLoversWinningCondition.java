package io.github.hellinfernal.werewolf.core.winningcondition;

import io.github.hellinfernal.werewolf.core.Game;
import io.github.hellinfernal.werewolf.core.player.Player;
import io.github.hellinfernal.werewolf.core.role.SpecialRole;

import java.util.List;

public class AmorLoversWinningCondition implements WinningCondition{
    @Override
    public boolean isSatisfied(Game game) {
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
