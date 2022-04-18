package io.github.hellinfernal.werewolf.core.game;

import io.github.hellinfernal.werewolf.core.Game;
import io.github.hellinfernal.werewolf.core.player.Player;
import io.github.hellinfernal.werewolf.core.user.GlobalPrinter;
import io.github.hellinfernal.werewolf.core.vote.ImperativVotingMachine;
import io.github.hellinfernal.werewolf.core.vote.VotingMachine;

import java.util.ArrayList;
import java.util.List;

public class WerewolfMove implements GameMove {
    private final Game _game;

    public WerewolfMove(final Game game) {
        _game = game;
    }

    /**
    public void startMove() {
        final List<Player> hunters = new ArrayList<>(_game.getAliveWerewolfPlayers());
        final List<Player> victims = _game.getAliveVillagerPlayers();

        final VotingMachine votingMachine = new VotingMachine(hunters, victims, (player, players) -> player.user().requestVillagerVote(players));
    }

    public void endMove() {
        //votingMachine.voteHighest().ifPresent(Player::kill);
    }

    public boolean active(){
        return false;
    }
    **/

    @Override
    public void execute() {
        _game.acceptGlobalPrinterMethod(GlobalPrinter::informAboutStartOfTheHunt);
        final List<Player> hunters = new ArrayList<>(_game.getAliveWerewolfPlayers());
        final List<Player> victims = _game.getAliveVillagerPlayers();

        final VotingMachine votingMachine = _game.get_voteStrategy(hunters, victims, (player, players) -> player.user().requestWerewolfVote(players));

        //TODO: warten bis eine antwort da ist
        /**
        while(!votingMachine.hasReceivedAnswers()) {

        }
         **/

        votingMachine.voteHighest().ifPresent(Player::kill);
        _game.acceptGlobalPrinterMethod(GlobalPrinter::informAboutEndOfTheHunt);
    }
}
