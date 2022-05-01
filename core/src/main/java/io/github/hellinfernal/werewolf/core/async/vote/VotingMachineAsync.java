package io.github.hellinfernal.werewolf.core.async.vote;

import io.github.hellinfernal.werewolf.core.async.player.PlayerAsync;
import io.github.hellinfernal.werewolf.core.player.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;

public class VotingMachineAsync {

    protected final List<PlayerAsync> _voters;
    protected final List<PlayerAsync> _playerSelection;
    protected final BiFunction<PlayerAsync, Collection<PlayerAsync>, PlayerAsync> _voteFunction;
    protected final Map<Player, AtomicLong> _playerVotes = new HashMap<>();

    public VotingMachineAsync(final List<PlayerAsync> voters, final List<PlayerAsync> playerSelection, final BiFunction<PlayerAsync, Collection<PlayerAsync>, PlayerAsync> voteFunction) {
        _voters = voters;
        _playerSelection = playerSelection;
        _voteFunction = voteFunction;
    }
}
