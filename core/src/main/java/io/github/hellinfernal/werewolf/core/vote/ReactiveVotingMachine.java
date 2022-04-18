package io.github.hellinfernal.werewolf.core.vote;

import io.github.hellinfernal.werewolf.core.player.Player;
import io.github.hellinfernal.werewolf.core.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.swing.text.html.parser.Entity;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class ReactiveVotingMachine extends VotingMachine {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReactiveVotingMachine.class);

    public ReactiveVotingMachine(List<Player> voters, List<Player> playerSelection, BiFunction<Player, Collection<Player>, Player> voteFunction) {
        super(voters, playerSelection, voteFunction);
    }

    @Override
    public Optional<Player> voteHighest() {
        return Flux.fromIterable(_voters)
                 .map(player -> _voteFunction.apply(player,_playerSelection))
                 .map(this::putInPlayerVotes)
                 .map(this::incrementPlayerVotes)
                 .then(getHighestVotedPlayer())
                 .blockOptional();
    }

    private synchronized Player incrementPlayerVotes(Player player) {
        _playerVotes.get(player).incrementAndGet();
        return player;
    }

    public synchronized Player putInPlayerVotes(Player player){
        _playerVotes.putIfAbsent(player, new AtomicLong(0));
        return player;
    }
    public synchronized Mono<Player> getHighestVotedPlayer(){
        Map.Entry<Player,AtomicLong> player = _playerVotes.entrySet()
                .stream()
                .sorted(Comparator.comparingLong(entry -> entry.getValue().get()))
                .peek(entry -> LOGGER.debug("Player: " + entry.getKey().user().name() + " Votes: " + entry.getValue().get()))
                .max(Comparator.comparingLong(entry -> entry.getValue().get()))
                .orElseThrow();
        Map.Entry<Player,AtomicLong> player2 = _playerVotes.entrySet()
                .stream()
                .sorted(Comparator.comparingLong(entry -> entry.getValue().get()))
                .filter(entry -> entry.getKey() != player.getKey())
                .peek(entry -> LOGGER.debug("Player: " + entry.getKey().user().name() + " Votes: " + entry.getValue().get()))
                .max(Comparator.comparingLong(entry -> entry.getValue().get()))
                .orElseThrow();

        if (player.getValue().get() > player2.getValue().get()){
            return Mono.just(player.getKey());
        }
        else {
            List<Player> newPlayerSelection = _playerVotes.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue().get() == player.getValue().get())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            VotingMachine votingMachine = new ReactiveVotingMachine(_voters,newPlayerSelection,_voteFunction);
            return Mono.just(votingMachine.voteHighest().get());
        }

        //This should never be reached.


    }

    /**static class VoteComperator implements Comparator<Map.Entry<Player,AtomicLong>>{


        @Override
        public int compare(Map.Entry<Player, AtomicLong> o1, Map.Entry<Player, AtomicLong> o2) {
            return Long.compare(o1.getValue().get(),o2.getValue().get());
        }
    } **/
}
