package io.github.hellinfernal.werewolf.core.game;

import io.github.hellinfernal.werewolf.core.Game;
import io.github.hellinfernal.werewolf.core.player.Player;

import static io.github.hellinfernal.werewolf.core.role.SpecialRole.Witch;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;


public class WitchMove1 implements GameMove {
    private final Set<Player> _witchesUsedRevivePotion = new HashSet<>();
    Game _game;

    public WitchMove1(Game game){
        _game = game;

    }
    @Override
    public void execute() {
        final Optional<Player> witch = _game.getSpecialClassPlayer(Witch);

        witch.ifPresent(playerWithWitchRole -> {
            // The witch in the game
            //Should be only called if the potion isnt used already
            Player lastKilledGuy = _game.getLastKilledPlayer();
            //the guy who is the latest killed guy. should be the last entry in the list
            boolean shouldHeBeRevived = playerWithWitchRole.user().requestDecisionAboutSavingLastKilledPlayer(lastKilledGuy);
            if ( shouldHeBeRevived ) {
                lastKilledGuy.revive();
                _witchesUsedRevivePotion.add(playerWithWitchRole);
            }
        });

    }
}
