package io.github.hellinfernal.werewolf.core.game;

import io.github.hellinfernal.werewolf.core.Game;
import io.github.hellinfernal.werewolf.core.player.Player;
import io.github.hellinfernal.werewolf.core.role.SpecialRole;
import io.github.hellinfernal.werewolf.core.vote.ImperativVotingMachine;
import io.github.hellinfernal.werewolf.core.vote.VotingMachine;

import java.util.List;
import java.util.Optional;

public class WitchMove2 implements GameMove{

    private final Game _game;

    public WitchMove2(Game game){
        _game = game;
    }

    @Override
    public void execute() {
        Optional<Player> playerWithWitchRole = _game.getSpecialClassPlayer(SpecialRole.Witch);
        if (playerWithWitchRole.isEmpty()) {
            return;
        }

        final List<Player> alivePlayers = _game.getAlivePlayers();
        final VotingMachine votingMachine = _game.get_voteStrategy(List.of(playerWithWitchRole.get()), alivePlayers, (player, players) -> player.user().requestKillPotionUse(players));

        votingMachine.voteHighest().ifPresent(Player::kill);
    }
}
