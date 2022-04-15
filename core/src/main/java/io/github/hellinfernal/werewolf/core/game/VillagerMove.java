package io.github.hellinfernal.werewolf.core.game;

import io.github.hellinfernal.werewolf.core.Game;
import io.github.hellinfernal.werewolf.core.player.Player;
import io.github.hellinfernal.werewolf.core.user.GlobalPrinter;
import io.github.hellinfernal.werewolf.core.vote.VotingMachine;

import java.util.List;


public class VillagerMove implements GameMove {

   private       Game                    _game;

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
      _game.acceptGlobalPrinterMethod(globalPrinter -> globalPrinter.informAboutResultOfVillagerVote(victim));
   }
}

