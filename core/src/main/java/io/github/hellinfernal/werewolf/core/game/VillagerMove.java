package io.github.hellinfernal.werewolf.core.game;

import io.github.hellinfernal.werewolf.core.Game;
import io.github.hellinfernal.werewolf.core.player.Player;
import io.github.hellinfernal.werewolf.core.vote.VotingMachine;

import java.util.List;


public class VillagerMove implements GameMove {

   private       Game                    _game;

   public VillagerMove( Game game ) {
      _game = game;
   }

   @Override
   public void execute() {
      final List<Player> alivePlayers = _game.getAlivePlayers();

      final VotingMachine votingMachine = new VotingMachine(alivePlayers, alivePlayers, (player, players) -> player.user().requestVillagerVote(players));

      votingMachine.voteHighest().ifPresent(Player::kill);
   }
}

