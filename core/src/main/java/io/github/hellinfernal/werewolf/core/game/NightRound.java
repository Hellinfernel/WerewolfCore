package io.github.hellinfernal.werewolf.core.game;

import io.github.hellinfernal.werewolf.core.Game;

public class NightRound implements GameRound {
    private final Game game;

    public NightRound(final Game game) {
        this.game = game;
    }
}
