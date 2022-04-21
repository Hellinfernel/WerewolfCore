package io.github.hellinfernal.werewolf.core.game;

import discord4j.core.GatewayDiscordClient;
import io.github.hellinfernal.werewolf.core.Game;
import io.github.hellinfernal.werewolf.core.player.Player;
import io.github.hellinfernal.werewolf.core.user.GlobalPrinter;
import io.github.hellinfernal.werewolf.core.vote.ImperativVotingMachine;
import io.github.hellinfernal.werewolf.core.vote.VotingMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;


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
      final VotingMachine votingMachine = _game.get_voteStrategy(alivePlayers,alivePlayers, (player, players) -> player.user().requestVillagerVote(players));

      // final VotingMachine votingMachine = new ImperativVotingMachine(alivePlayers, alivePlayers, (player, players) -> player.user().requestVillagerVote(players));
      Player victim = votingMachine.voteHighest().get();
      victim.kill();
      _game.acceptGlobalPrinterMethod(globalPrinter -> globalPrinter.debugPrint("Victim Killed: " + victim.toString()));
      _game.acceptGlobalPrinterMethod(globalPrinter -> globalPrinter.informAboutResultOfVillagerVote(victim));
   }
}

