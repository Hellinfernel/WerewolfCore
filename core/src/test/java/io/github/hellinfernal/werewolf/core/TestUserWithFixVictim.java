package io.github.hellinfernal.werewolf.core;

import io.github.hellinfernal.werewolf.core.player.Player;

import java.util.Set;

public class TestUserWithFixVictim extends TestUser{
    private final String _fixVictim;
    public TestUserWithFixVictim(String fixVictim){
        super();
        _fixVictim = fixVictim;

    }

    @Override
    public Player requestVillagerVote(Set<Player> potentialTargets) {
        Player target = potentialTargets.stream().filter(player -> player.user().name() == _fixVictim).findFirst().orElseThrow();
        return super.requestVillagerVote(potentialTargets);
    }
}
