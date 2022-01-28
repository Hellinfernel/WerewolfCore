package io.github.hellinfernal.werewolf.core;

public class Werewolf extends Villager{

    public boolean WinningCondition(WerewolfGame werewolfGame){ //Basically, if there are more non-Werewolfes then Werewolfes, then false is returned. the Werewolfes win, if they are more.
        if (werewolfGame.allPlayers.stream()
                .filter(player -> player.isAlive == true)
                .filter(player -> player.gameRole
                        .getClass()
                        .isAssignableFrom(Werewolf.class) == true)
                .count() < werewolfGame.allPlayers.stream()
                .filter(player -> player.isAlive)
                .filter(player -> player.gameRole.getClass().isAssignableFrom(Werewolf.class) == false)
                .count()){
            return false;
        }

        else{
            return true;
        }
    }
    public int howMuchOfThisRole(WerewolfGame werewolfGame){ //i dont know what i am doing, but at normal, the number of werewolfes should be about a 4tel of the total nu,ber of players.
      return werewolfGame.sizeOfTheGame / 4;
    }
}
