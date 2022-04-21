package io.github.hellinfernal.werewolf.core.vote;

import io.github.hellinfernal.werewolf.core.Game;
import io.github.hellinfernal.werewolf.core.player.Player;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;

public abstract class VotingMachine {
    protected final List<Player> _voters;
    protected final List<Player> _playerSelection;
    protected final BiFunction<Player, Collection<Player>, Player> _voteFunction;
    protected final Map<Player, AtomicLong> _playerVotes = new HashMap<>();
    protected final Game _game;

    public VotingMachine(final List<Player> voters, final List<Player> playerSelection, final BiFunction<Player, Collection<Player>, Player> voteFunction, Game game) {
        _voters = voters;
        _playerSelection = playerSelection;
        _voteFunction = voteFunction;
        _game = game;
    }

    public abstract Optional<Player> voteHighest();
}
