package io.github.hellinfernal.werewolf.core;

public class Player { //A player is... well, a Player.
    GameRole gameRole; //The role of the Player
    String name; //His name
    boolean isAlive; //If its true, he is alive. if false, then... well, not.
    public Player(PlayerPreInitialise preInitialise, GameRole gameRole){
        name = preInitialise.name;
        this.gameRole = gameRole;
        isAlive = true;


    }


}
