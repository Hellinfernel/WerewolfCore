package io.github.hellinfernal.werewolf.core.game;

import io.github.hellinfernal.werewolf.core.Game;
import io.github.hellinfernal.werewolf.core.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class WerewolfMove implements GameMove {
    private final Game _game;

    private final Map<Player, AtomicLong> _huntingTargets = new HashMap<>();
    //all Players who can be killed by a Vote.
    private final List<Player> _hunters = new ArrayList<>();
    // All voters. If their boolean is true, they still have a vote.


    public WerewolfMove(final Game game) {

        _game = game;
    }



    @Override
    public void execute() {

        _huntingTargets.clear();
        _hunters.clear();
        _game.getAliveVillagerPlayers().forEach(player -> _huntingTargets.put(player, new AtomicLong()));
        _hunters.addAll(_game.getAliveWerewolfPlayers());

        _hunters.forEach(voters -> voteProcess(voters, _huntingTargets));
        final AtomicReference<Player> highestVotedPlayer = new AtomicReference<>(null);
        AtomicLong votesHighest = new AtomicLong();

        _huntingTargets.forEach((player, votes) -> {
            //finds the player who has the most votes
            if (highestVotedPlayer.get() == null) {
                highestVotedPlayer.set(player);
            } else {
                votesHighest.set(_huntingTargets.get(highestVotedPlayer.get()).get());
                long votesCurrent = votes.get();
                if (votesCurrent > votesHighest.get()) {
                    highestVotedPlayer.set(player);
                }
            }
        });

        if (_huntingTargets.entrySet()
                .stream()
                .filter(p -> p.getValue().get() == votesHighest.get())
                .count() > 1){
            final Map<Player,AtomicLong> SecondVoteMap = new HashMap<>();
            //second voteMap for a second Vote
            _huntingTargets.entrySet()
                    .stream()
                    .filter(p -> p.getValue().get() == votesHighest.get())
                    .forEach(player -> SecondVoteMap.put(player.getKey(),new AtomicLong()));
            //Adds all who have the same number of votes as that one with the highest votes
            _hunters.forEach(player -> voteProcess(player,SecondVoteMap));

            SecondVoteMap.forEach((player, votes) -> {
                //finds the player who has the most votes
                if (highestVotedPlayer.get() == null) {
                    highestVotedPlayer.set(player);
                } else {
                    votesHighest.set(_huntingTargets.get(highestVotedPlayer.get()).get());
                    long votesCurrent = votes.get();
                    if (votesCurrent > votesHighest.get()) {
                        highestVotedPlayer.set(player);
                    }
                }
            });






        }
        //checks if there are more than one player with the most votes
        highestVotedPlayer.get().kill();
    }

    private void voteProcess(Player player, Map<Player, AtomicLong> voteMap) {
        //unsure if that actually changes the overgiven Map
        final Player votedPlayer = player.user().requestVillagerVote(voteMap.keySet());
        if (votedPlayer == null) {
            return;
        }
        final AtomicLong votes = voteMap.get(votedPlayer);
        if ( votes == null ) {
            throw new IllegalStateException("voted player is not alive. This should not happen");
        }
        votes.incrementAndGet();
    }
}
