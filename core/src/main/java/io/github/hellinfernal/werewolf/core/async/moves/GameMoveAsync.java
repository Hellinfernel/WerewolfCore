package io.github.hellinfernal.werewolf.core.async.moves;

public interface GameMoveAsync {

    /**
     * The method who is used if the GameMove is called :D
     */
    void execute();

    /**
     * the method who is called as the move starts
     */
    void start();
}
