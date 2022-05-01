package io.github.hellinfernal.werewolf.core.async.moves;

import java.util.List;
import java.util.Optional;

import io.github.hellinfernal.werewolf.core.async.GameAsync;
import io.github.hellinfernal.werewolf.core.player.Player;
import io.github.hellinfernal.werewolf.core.role.SpecialRole;
import io.github.hellinfernal.werewolf.core.vote.VotingMachine;

public class WitchMoveAsync2 implements GameMoveAsync {

    private final GameAsync _game;

    public WitchMoveAsync2(GameAsync game){
        _game = game;
    }

    @Override
    public MovePriority movePriority() {
        return MovePriority.WITCH_MOVE2;
    }

    @Override
    public void execute() {
        Optional<Player> playerWithWitchRole = _game.getSpecialClassPlayer(SpecialRole.Witch);
        if (playerWithWitchRole.isEmpty()) {
            return;
        }

        final List<Player> alivePlayers = _game.getAlivePlayers();
        final VotingMachine votingMachine = _game.getVoteStrategy(List.of(playerWithWitchRole.get()), alivePlayers, (player, players) -> player.user().requestKillPotionUse(players));

        votingMachine.voteHighest().ifPresent(Player::kill);
    }

    @Override
    public void start() {

    }
}
