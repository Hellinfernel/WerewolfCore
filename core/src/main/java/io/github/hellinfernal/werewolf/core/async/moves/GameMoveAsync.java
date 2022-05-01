package io.github.hellinfernal.werewolf.core.async.moves;

import java.time.Instant;

public interface GameMoveAsync {

    /**
     *
     * @return the moment where this move started.
     */
    Instant startOfstart();

    /**
     *
     * @return the Priority which the move has. Moves who are added by a event should have a high priority.
     */
    MovePriority movePriority();

    /**
     *
     * @return the state of the Move
     */
    MoveState actualState();

    /**
     * The method who is used if the GameMove is called :D
     */
    void execute();

    /**
     * the method who is called as the move starts
     */
    void start();

    /**
     *  finishes the Move regularly.
     */
    void finish();

    /**
     * finishes the Move abruptly, it needs to get finished now.
     */
    void forcedFinish();
}
