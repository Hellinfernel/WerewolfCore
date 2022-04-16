package io.github.hellinfernal.werewolf.core.game;

import io.github.hellinfernal.werewolf.core.Game;
import io.github.hellinfernal.werewolf.core.player.Player;
import io.github.hellinfernal.werewolf.core.user.GlobalPrinter;
import io.github.hellinfernal.werewolf.core.vote.VotingMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class VillagerMove implements GameMove {

   private       Game                    _game;
   private Logger LOGGER = LoggerFactory.getLogger(VillagerMove.class);

   public VillagerMove( Game game ) {
      _game = game;
   }

   @Override
   public void execute() {
      _game.acceptGlobalPrinterMethod(GlobalPrinter::informAboutStartOfTheVillagerVote);

      final List<Player> alivePlayers = _game.getAlivePlayers();

      final VotingMachine votingMachine = new VotingMachine(alivePlayers, alivePlayers, (player, players) -> player.user().requestVillagerVote(players));
      Player victim = votingMachine.voteHighest().get();
      victim.kill();
      LOGGER.debug("Victim Killed: " + victim.toString());
      _game.acceptGlobalPrinterMethod(globalPrinter -> globalPrinter.informAboutResultOfVillagerVote(victim));
   }
}

