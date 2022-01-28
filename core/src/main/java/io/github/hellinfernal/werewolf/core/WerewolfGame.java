package io.github.hellinfernal.werewolf.core;

import io.github.hellinfernal.werewolf.core.Player;

import java.util.ArrayList;
import java.util.List;

public class WerewolfGame {

    ArrayList<Player> allPlayers; //that is a List of all players who are part of this game.
    public int sizeOfTheGame;
    public WerewolfGame(List<PlayerPreInitialise> players,List<GameRole> gameRoles){
        sizeOfTheGame = players.size();
        allPlayers = players.stream().forEach(p -> new Player(p,gameRoles.)); //here should everyone get a role.


    }

}
