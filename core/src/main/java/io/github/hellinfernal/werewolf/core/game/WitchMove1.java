package io.github.hellinfernal.werewolf.core.game;

import io.github.hellinfernal.werewolf.core.Game;
import io.github.hellinfernal.werewolf.core.player.Player;

import static io.github.hellinfernal.werewolf.core.role.SpecialRole.Witch;

public class WitchMove1 implements GameMove {
    Game _game;

    public WitchMove1(Game game){
        _game = game;

    }
    @Override
    public void execute() {

        Player witch = _game.getSpecialClassPlayer(Witch);
        // The witch in the game
        if (witch.specialRoles().stream().filter(specialRole -> specialRole == Witch).findFirst().g.hasHealPotion == true) {
            //Should be only called if the potion isnt used already
            Player lastKilledGuy = _game.getKilledPlayers().get(_game.getKilledPlayers().size());
            //the guy who is the latest killed guy. should be the last entry in the list
            boolean shouldHeBeRevived = witch.user().requestDecisionAboutSavingLastKilledPlayer(lastKilledGuy);
            if (shouldHeBeRevived == true) {
                witch.getSpecialRoleAsObject.useHealPotion(lastKilledGuy);
            }
        }


    }
}
