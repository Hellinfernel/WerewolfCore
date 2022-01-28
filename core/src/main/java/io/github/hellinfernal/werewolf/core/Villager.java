package io.github.hellinfernal.werewolf.core;

public class Villager extends GameRole{


    @Override
    public boolean WinningCondition(WerewolfGame werewolfGame) { //if there is still a Werewolf alive, then the villagers dont
        if (werewolfGame.allPlayers.stream()
                .filter(player -> player.isAlive = true)
                .anyMatch(player -> player
                        .gameRole
                        .getClass()
                        .isAssignableFrom(Werewolf.class))){
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    public int howMuchOfThisRole(WerewolfGame werewolfGame) { //basically, this should be fill up the rest of the slots.
        werewolfGame.sizeOfTheGame
        return 0;
    }


}
