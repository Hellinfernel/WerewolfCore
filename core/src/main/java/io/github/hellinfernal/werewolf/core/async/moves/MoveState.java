package io.github.hellinfernal.werewolf.core.async.moves;

public enum MoveState {

    /**
     * The base state of every Move.
     */
    NOT_STARTED(),
    /**
     * The Moves is still starting.
     */
    INITIALIZING(),
    /**
     * The Move waits for potential input
     */
    WAITING(),
    /**
     * The move has its result, it can be finished regularly :D
     */
    READY_TO_RETURN(),
    /**
     * The Move finished its job :D
     */
    FINISHED(),
    /**
     * When something got wrong :F
     */
    ERROR();

    MoveState(){

    }
}
