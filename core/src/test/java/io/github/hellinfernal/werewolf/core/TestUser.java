package io.github.hellinfernal.werewolf.core;

import io.github.hellinfernal.werewolf.core.player.Player;
import io.github.hellinfernal.werewolf.core.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;


public class TestUser implements User {
    private final String                       _name;
    private Predicate<Player> _hunterVote;
    private Predicate<Player> _vote;
    private Predicate<Player> _reanimationVote = p -> false;
    private Predicate<Player> _killPotionVote = p -> false;
    private Predicate<Player> _loverVote = p -> false;


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

    public void set_reanimationVote(Predicate<Player> reanimationVote){
        _reanimationVote = reanimationVote;
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
        return _reanimationVote.test(lastKilledGuy);
    }

    @Override
    public Player requestKillPotionUse(Set<Player> possibleVictims) {
        return possibleVictims.stream()
                .filter(_killPotionVote)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void informAboutFallingInLove(Player lover) {
        System.out.println("yo, you are in love with " + lover.toString() + " ,ok?");
    }

    @Override
    public Player requestLover(List<Player> players) {
        return players.stream()
                .filter(_loverVote)
                .findFirst()
                .orElse(null);
    }

    public void set_killPotionVote(Predicate<Player> killPotionVote) {
        _killPotionVote = killPotionVote;
    }

    /**
     *
     * sets the filter for requestLover
     */
    public void set_loverVote(Predicate<Player> filter){
        _loverVote = filter;

    }

    /**
     * sets the filter for _hunterVote
     * @param fitler the filter.
     */
    public void set_hunterVote(Predicate<Player> fitler){
        _hunterVote = fitler;
    }
}
