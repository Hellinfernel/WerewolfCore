package io.github.hellinfernal.werewolf.core.async.moves;

import static io.github.hellinfernal.werewolf.core.role.SpecialRole.Witch;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import io.github.hellinfernal.werewolf.core.Game;
import io.github.hellinfernal.werewolf.core.async.GameAsync;
import io.github.hellinfernal.werewolf.core.async.player.PlayerAsync;
import io.github.hellinfernal.werewolf.core.player.Player;


public class WitchMoveAsync1 implements GameMoveAsync {
    private final Set<PlayerAsync> _witchesUsedRevivePotion = new HashSet<>();
    GameAsync _game;

    public WitchMoveAsync1(GameAsync game){
        _game = game;

    }
    @Override
    public void execute() {
        final Optional<PlayerAsync> witch = _game.getSpecialClassPlayer(Witch);

        witch.ifPresent(playerWithWitchRole -> {
            if (!_witchesUsedRevivePotion.contains(witch.get())) {
                // The witch in the game
                //Should be only called if the potion isnt used already
                PlayerAsync lastKilledGuy = _game.getLastKilledPlayer();
                //the guy who is the latest killed guy. should be the last entry in the list
                boolean shouldHeBeRevived = playerWithWitchRole.user().requestDecisionAboutSavingLastKilledPlayer(lastKilledGuy);
                if (shouldHeBeRevived) {
                    lastKilledGuy.revive();
                    _witchesUsedRevivePotion.add(playerWithWitchRole);
                }
            }
        });

    }
}
