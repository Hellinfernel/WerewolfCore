package io.github.hellinfernal.werewolf.core.vote;

import io.github.hellinfernal.werewolf.core.player.Player;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;

public abstract class VotingMachine {
    protected final List<Player> _voters;
    protected final List<Player> _playerSelection;
    protected final BiFunction<Player, Collection<Player>, Player> _voteFunction;
    protected final Map<Player, AtomicLong> _playerVotes = new HashMap<>();

    public VotingMachine(final List<Player> voters, final List<Player> playerSelection, final BiFunction<Player, Collection<Player>, Player> voteFunction) {
        _voters = voters;
        _playerSelection = playerSelection;
        _voteFunction = voteFunction;
    }

    public abstract Optional<Player> voteHighest();
}
