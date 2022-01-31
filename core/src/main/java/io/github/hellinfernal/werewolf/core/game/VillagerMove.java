package io.github.hellinfernal.werewolf.core.game;

import io.github.hellinfernal.werewolf.core.Game;
import io.github.hellinfernal.werewolf.core.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class VillagerMove implements GameMove {
    private Game _game;
    HashMap<Player,Integer> potentialTargets = new HashMap<>();
    //all Players who can be killed by a Vote.

    HashMap<Player,Boolean> voters = new HashMap<>();
    // All voters. If their boolean is true, they still have a vote.

    AtomicReference<Player> finalVictim = null;
    //this is the player who should be killed.

    public VillagerMove(Game game){
        _game = game;
    }


    @Override
    public void execute() {
        HashMap<Player,Integer> potentialTargets = new HashMap<>();
        //all Players who can be killed by a Vote.
        _game.getAlivePlayers().stream().forEach(player -> potentialTargets.put(player,0));
        HashMap<Player,Boolean> voters = new HashMap<>();
        // All voters. If their boolean is true, they still have a vote.
        _game.getAlivePlayers().stream().forEach(player -> voters.put(player,true));
        AtomicReference<Player> finalVictim = null;
        //this is the player who should be killed.
        
        voters.keySet().stream().forEach(player -> voteProcess(player.user().requestVillagerVote(potentialTargets.keySet())));
        // requests all users who are allowed to vote to give a vote


    }
    private void voteProcess(Player player){

            Player target = player.user().requestVillagerVote(potentialTargets.keySet());
            //the target is the player who got the vote from a voting player.
            if(potentialTargets.containsKey(target)){
                potentialTargets.replace(target,potentialTargets.get(target) + 1);
                voters.replace(player,false);
                long remainingVotes = voters.entrySet().stream().filter(x -> x.getValue() == true).count();
                int highestValue = potentialTargets.entrySet().stream().sorted().mapToInt(Map.Entry::getValue).max().orElse(0);
                //finds the highest voted Player and gets his number of votes
                int secondHighestValue = potentialTargets.values().stream().mapToInt(x -> x).filter(x -> x < highestValue).filter(x -> x >= 0).max().orElse(0);
                //finds the Value who is the second highest.
                if(highestValue - secondHighestValue > remainingVotes && potentialTargets.values().stream().filter(x -> x == highestValue).count() == 1){
                    //this is called if the remaining Votes wouldnt change the final result :D
                    finalVictim.set(potentialTargets.entrySet().stream().filter(x -> x.getValue() == highestValue).findFirst().get().getKey());
                    //sets the victim.
                }
                else if(remainingVotes == 0){
                    //this should be called if there are no remaining votes but there are more than 1 with the highest
                    finalVictim.set(potentialTargets.entrySet().stream().filter(x -> x.getValue() == highestValue).findAny().get().getKey());
                    // sets the Victim. Maybe i choose to add a second voting if there is a tie :D
                }
                if (finalVictim.get() != null){
                    _game.getPlayers().stream().forEach(thatPlayer -> thatPlayer.user().informAboutResultOfVillagerVote(finalVictim.get()));

                }
            }
            else {
                //if they returned a not valid target, force them to try again :D
            }


        }

    }

