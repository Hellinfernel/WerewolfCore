package io.github.hellinfernal.werewolf.core.async.moves;

import java.time.Instant;
import java.util.List;

import io.github.hellinfernal.werewolf.core.async.player.PlayerAsync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.hellinfernal.werewolf.core.async.GameAsync;
import io.github.hellinfernal.werewolf.core.player.Player;
import io.github.hellinfernal.werewolf.core.user.GlobalPrinter;
import io.github.hellinfernal.werewolf.core.vote.VotingMachine;


public class VillagerMoveAsync implements GameMoveAsync {

   private       GameAsync                    _game;
   private static Logger LOGGER = LoggerFactory.getLogger(VillagerMoveAsync.class);

   public VillagerMoveAsync( GameAsync game ) {
      _game = game;
   }

   @Override
   public Instant startOfstart() {
      return null;
   }

   @Override
   public MovePriority movePriority() {
      return MovePriority.VILLAGER_MOVE;
   }

   @Override
   public MoveState actualState() {
      return null;
   }

   @Override
   public void execute() {
      _game.acceptGlobalPrinterMethod(t -> GlobalPrinter.informAboutStartOfTheVillagerVote(t));

      final List<PlayerAsync> alivePlayers = _game.getAlivePlayers();
      final VotingMachine votingMachine = _game.getVoteStrategy(alivePlayers,alivePlayers, (player, players) -> player.user().requestVillagerVote(players));

      // final VotingMachine votingMachine = new ImperativVotingMachine(alivePlayers, alivePlayers, (player, players) -> player.user().requestVillagerVote(players));
      Player victim = votingMachine.voteHighest().get();
      victim.kill();
      LOGGER.debug("Victim Killed: " + victim.toString());
      _game.acceptGlobalPrinterMethod(globalPrinter -> globalPrinter.informAboutResultOfVillagerVote(victim));
   }

   @Override
   public void start() {

   }

   @Override
   public void finish() {

   }

   @Override
   public void forcedFinish() {

   }
}

