package io.github.hellinfernal.werewolf.core;

import io.github.hellinfernal.werewolf.core.player.Player;
import io.github.hellinfernal.werewolf.core.user.User;

import java.util.Set;
import java.util.UUID;

public class TestUser implements User {
    private final String _name;

    public TestUser() {
        _name = UUID.randomUUID().toString();
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
        Player votedTarget;
        votedTarget = potentialTargets.stream().findFirst().get();
        System.out.println("TestUser " + _name + " voted for " + votedTarget.user().name());
        return votedTarget;
    }

    @Override
    public void informAboutResultOfVillagerVote(Player killedPlayer) {
        System.out.println(killedPlayer + " was killed.");

    }
}
