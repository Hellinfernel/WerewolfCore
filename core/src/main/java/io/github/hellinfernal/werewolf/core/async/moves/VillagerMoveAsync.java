package io.github.hellinfernal.werewolf.core.async.moves;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.hellinfernal.werewolf.core.async.GameAsync;
import io.github.hellinfernal.werewolf.core.player.Player;
import io.github.hellinfernal.werewolf.core.user.GlobalPrinter;
import io.github.hellinfernal.werewolf.core.vote.VotingMachine;


public class VillagerMoveAsync implements GameMoveAsync {

   private       GameAsync                    _game;
   private Logger LOGGER = LoggerFactory.getLogger(VillagerMoveAsync.class);

   public VillagerMoveAsync( GameAsync game ) {
      _game = game;
   }

   @Override
   public void execute() {
      _game.acceptGlobalPrinterMethod(GlobalPrinter::informAboutStartOfTheVillagerVote);

      final List<Player> alivePlayers = _game.getAlivePlayers();
      final VotingMachine votingMachine = _game.getVoteStrategy(alivePlayers,alivePlayers, (player, players) -> player.user().requestVillagerVote(players));

      // final VotingMachine votingMachine = new ImperativVotingMachine(alivePlayers, alivePlayers, (player, players) -> player.user().requestVillagerVote(players));
      Player victim = votingMachine.voteHighest().get();
      victim.kill();
      LOGGER.debug("Victim Killed: " + victim.toString());
      _game.acceptGlobalPrinterMethod(globalPrinter -> globalPrinter.informAboutResultOfVillagerVote(victim));
   }
}

