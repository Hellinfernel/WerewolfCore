package io.github.hellinfernal.werewolf.core.game;

import io.github.hellinfernal.werewolf.core.Game;
import io.github.hellinfernal.werewolf.core.player.Player;
import io.github.hellinfernal.werewolf.core.vote.VotingMachine;

import java.util.ArrayList;
import java.util.List;

public class WerewolfMove implements GameMove {
    private final Game _game;

    public WerewolfMove(final Game game) {
        _game = game;
    }

    @Override
    public void execute() {
        final List<Player> hunters = new ArrayList<>(_game.getAliveWerewolfPlayers());
        final List<Player> victims = _game.getAliveVillagerPlayers();

        final VotingMachine votingMachine = new VotingMachine(hunters, victims, (player, players) -> player.user().requestVillagerVote(players));

        final Player votedPlayer = votingMachine.vote();
        votedPlayer.kill();
    }
}
