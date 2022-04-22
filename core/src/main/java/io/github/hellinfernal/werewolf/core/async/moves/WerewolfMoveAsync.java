package io.github.hellinfernal.werewolf.core.async.moves;

import java.util.ArrayList;
import java.util.List;

import io.github.hellinfernal.werewolf.core.async.GameAsync;
import io.github.hellinfernal.werewolf.core.player.Player;
import io.github.hellinfernal.werewolf.core.user.GlobalPrinter;
import io.github.hellinfernal.werewolf.core.vote.VotingMachine;

public class WerewolfMoveAsync implements GameMoveAsync {
    private final GameAsync _game;

    public WerewolfMoveAsync(final GameAsync game) {
        _game = game;
    }

    @Override
    public void execute() {
        _game.acceptGlobalPrinterMethod(GlobalPrinter::informAboutStartOfTheHunt);
        final List<Player> hunters = new ArrayList<>(_game.getAliveWerewolfPlayers());
        final List<Player> victims = _game.getAliveVillagerPlayers();

        final VotingMachine votingMachine = _game.getVoteStrategy(hunters, victims, (player, players) -> player.user().requestWerewolfVote(players));

        //TODO: warten bis eine antwort da ist
        /**
        while(!votingMachine.hasReceivedAnswers()) {

        }
         **/

        votingMachine.voteHighest().ifPresent(Player::kill);
        _game.acceptGlobalPrinterMethod(GlobalPrinter::informAboutEndOfTheHunt);
    }
}
