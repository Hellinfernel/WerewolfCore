package io.github.hellinfernal.werewolf.core;

import io.github.hellinfernal.werewolf.core.player.Player;
import io.github.hellinfernal.werewolf.core.user.User;

import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;


public class TestUser implements User {
    private final String                       _name;
    private final Function<Set<Player>,Player> _votedPlayer;

    public TestUser(final String name, final Function<Set<Player>,Player> votedPlayer ) {
        _name = name;
        _votedPlayer = votedPlayer;
    }

    @Override
    public String toString() {
        return "Testuser: " + _name;
    }

    public TestUser() {
        this(UUID.randomUUID().toString(), v -> null);
    }


    @Override
    public String name() {
        return _name;
    }

    @Override
    public void tell(final String whatTodo) {
        System.out.println(whatTodo);

    }

    @Override
    public Player requestVillagerVote(Set<Player> potentialTargets) {
        return _votedPlayer.apply(potentialTargets);
    }

    @Override
    public void informAboutResultOfVillagerVote(Player killedPlayer) {
        System.out.println(killedPlayer + " was killed.");

    }
}
