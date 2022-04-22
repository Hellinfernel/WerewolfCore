package io.github.hellinfernal.werewolf.core.async.winningcondition;

import io.github.hellinfernal.werewolf.core.Game;
import io.github.hellinfernal.werewolf.core.async.GameAsync;


public interface WinningConditionAsync {


    boolean isSatisfied(final GameAsync game);
}
