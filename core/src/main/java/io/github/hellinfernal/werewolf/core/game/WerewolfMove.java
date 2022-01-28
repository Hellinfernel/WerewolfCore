package io.github.hellinfernal.werewolf.core.game;

import io.github.hellinfernal.werewolf.core.Game;

public class WerewolfMove implements GameMove {
    private final Game game;

    public WerewolfMove(final Game game) {

        this.game = game;
    }
}
