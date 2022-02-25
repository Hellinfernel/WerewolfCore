package io.github.hellinfernal.werewolf.core.vote;

import io.github.hellinfernal.werewolf.core.player.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;

public class VotingMachine {
    private final List<Player> _voters;
    private final List<Player> _playerSelection;
    private final BiFunction<Player, Collection<Player>, Player> _voteFunction;
    private final Map<Player, AtomicLong> _playerVotes = new HashMap<>();

    public VotingMachine(final List<Player> voters, final List<Player> playerSelection, final BiFunction<Player, Collection<Player>, Player> voteFunction) {
        _voters = voters;
        _playerSelection = playerSelection;
        _voteFunction = voteFunction;
        playerSelection.forEach(p -> _playerVotes.computeIfAbsent(p, i -> new AtomicLong()));
    }

    public Player vote() {
        _voters.forEach(voters -> voteProcess(voters, _playerVotes));
        final AtomicReference<Player> highestVotedPlayer = new AtomicReference<>(null);
        AtomicLong votesHighest = new AtomicLong();

        _playerVotes.forEach((player, votes) -> {
            //finds the player who has the most votes
            if (highestVotedPlayer.get() == null) {
                highestVotedPlayer.set(player);
            } else {
                votesHighest.set(_playerVotes.get(highestVotedPlayer.get()).get());
                long votesCurrent = votes.get();
                if (votesCurrent > votesHighest.get()) {
                    highestVotedPlayer.set(player);
                }
            }
        });

        if (_playerVotes.entrySet()
                .stream()
                .filter(p -> p.getValue().get() == votesHighest.get())
                .count() > 1) {
            final Map<Player, AtomicLong> SecondVoteMap = new HashMap<>();
            //second voteMap for a second Vote
            _playerVotes.entrySet()
                    .stream()
                    .filter(p -> p.getValue().get() == votesHighest.get())
                    .forEach(player -> SecondVoteMap.put(player.getKey(), new AtomicLong()));
            //Adds all who have the same number of votes as that one with the highest votes
            _voters.forEach(player -> voteProcess(player, SecondVoteMap));

            SecondVoteMap.forEach((player, votes) -> {
                //finds the player who has the most votes
                if (highestVotedPlayer.get() == null) {
                    highestVotedPlayer.set(player);
                } else {
                    votesHighest.set(_playerVotes.get(highestVotedPlayer.get()).get());
                    long votesCurrent = votes.get();
                    if (votesCurrent > votesHighest.get()) {
                        highestVotedPlayer.set(player);
                    }
                }
            });
        }
        //checks if there are more than one player with the most votes
        return highestVotedPlayer.get();
    }

    private void voteProcess(Player player, Map<Player, AtomicLong> voteMap) {
        //unsure if that actually changes the overgiven Map
        final Player votedPlayer = _voteFunction.apply(player, voteMap.keySet());
        if (votedPlayer == null) {
            return;
        }
        final AtomicLong votes = voteMap.get(votedPlayer);
        if (votes == null) {
            throw new IllegalStateException("voted player is not alive. This should not happen");
        }
        votes.incrementAndGet();
    }
}
