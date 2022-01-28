package io.github.hellinfernal.werewolf.core.winningcondition;

import io.github.hellinfernal.werewolf.core.Game;

public interface WinningCondition {
    boolean isSatisfied(final Game game);
}
