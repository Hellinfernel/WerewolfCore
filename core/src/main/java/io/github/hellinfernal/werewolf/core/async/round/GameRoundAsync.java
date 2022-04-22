package io.github.hellinfernal.werewolf.core.async.round;

import io.github.hellinfernal.werewolf.core.async.moves.GameMoveAsync;

import java.util.Collection;

public interface GameRoundAsync {
    Collection<GameMoveAsync> getMoves();
}
