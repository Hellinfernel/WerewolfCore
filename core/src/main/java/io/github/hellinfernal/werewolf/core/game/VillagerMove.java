package io.github.hellinfernal.werewolf.core.game;

import io.github.hellinfernal.werewolf.core.Game;
import io.github.hellinfernal.werewolf.core.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;


public class VillagerMove implements GameMove {

   private       Game                    _game;
   private final Map<Player, AtomicLong> _votesByPlayer = new HashMap<>();
   //all Players who can be killed by a Vote.
   private final List<Player>            _voters        = new ArrayList<>();
   // All voters. If their boolean is true, they still have a vote.

   public VillagerMove( Game game ) {
      _game = game;
   }

   @Override
   public void execute() {
      _votesByPlayer.clear();
      _voters.clear();
      _game.getAlivePlayers().forEach(player -> _votesByPlayer.put(player, new AtomicLong()));
      _voters.addAll(_game.getAlivePlayers());

      _voters.forEach(this::voteProcess);
      final AtomicReference<Player> highestVotedPlayer = new AtomicReference<>(null);

      _votesByPlayer.forEach((player, votes) -> {
         if (highestVotedPlayer.get() == null) {
            highestVotedPlayer.set(player);
         } else {
            long votesHighest = _votesByPlayer.get(highestVotedPlayer.get()).get();
            long votesCurrent = votes.get();
            if (votesCurrent > votesHighest) {
               highestVotedPlayer.set(player);
            }
         }
      });

      highestVotedPlayer.get().kill();
   }

   private void voteProcess( Player player ) {
      final Player votedPlayer = player.user().requestVillagerVote(_votesByPlayer.keySet());
      if (votedPlayer == null) {
         return;
      }
      final AtomicLong votes = _votesByPlayer.get(votedPlayer);
      if ( votes == null ) {
         throw new IllegalStateException("voted player is not alive. This should not happen");
      }
      votes.incrementAndGet();
   }
}

