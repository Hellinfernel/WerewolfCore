package io.github.hellinfernal.werewolf.core;

import io.github.hellinfernal.werewolf.core.player.Player;
import io.github.hellinfernal.werewolf.core.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;


public class TestUser implements User {
    private final String                       _name;
    private Predicate<Player> _vote;

    public TestUser(final String name, final  Predicate<Player> vote ) {
        _name = name;
        _vote = getVote(vote);
    }

    @NotNull
    private Predicate<Player> getVote(Predicate<Player> vote) {
        if (vote == null){
            return p -> false;
        }
        else {
            return vote;
        }

    }

    public TestUser(final String name){
        _name = name;
        _vote = p -> false;
    }

    public void changeVote(Predicate<Player> vote){
        _vote = vote;
    }


    @Override
    public String toString() {
        return "Testuser: " + _name;
    }

    public TestUser() {
        this(UUID.randomUUID().toString(), v -> false);
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
    public Player requestVillagerVote(Set<Player> playersToVoteFrom) {
        return playersToVoteFrom.stream()
              .filter(_vote)
              .findFirst()
              .orElse(null);
    }

    @Override
    public Player requestWerewolfVote(Set<Player> playersToVoteFrom) {
        return playersToVoteFrom.stream()
              .filter(_vote)
              .findFirst()
              .orElse(null);
    }

    @Override
    public void informAboutResultOfVillagerVote(Player killedPlayer) {
        System.out.println(killedPlayer + " was killed.");

    }

    @Override
    public void informAboutGameEnd() {
        System.out.println("game ends :D");
    }

    @Override
    public boolean requestDecisionAboutSavingLastKilledPlayer(Player lastKilledGuy) {
        if (lastKilledGuy == _vote)
    }
}
