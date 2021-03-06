package io.github.hellinfernal.werewolf.core.vote;

import io.github.hellinfernal.werewolf.core.player.Player;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

public class VoteMachineFactory {
    VoteMachineFactory.Machines _machine;
    public VoteMachineFactory(VoteMachineFactory.Machines machine){
        _machine = machine;
    }

    public enum Machines{
        REACTIVE_MACHINE,
        IMPERATIV_MACHINE


    }
    public VotingMachine generateVotingMachine(List<Player> voters, List<Player> playerSelection, BiFunction<Player, Collection<Player>,Player> votingFunction){
        switch (_machine){
            case IMPERATIV_MACHINE:
                return new ImperativVotingMachine(voters,playerSelection,votingFunction);
            case REACTIVE_MACHINE:
                return new ReactiveVotingMachine(voters,playerSelection,votingFunction);
            default:
                throw new RuntimeException();
        }
    }
}
