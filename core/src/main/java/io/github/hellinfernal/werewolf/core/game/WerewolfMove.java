package io.github.hellinfernal.werewolf.core.game;

import io.github.hellinfernal.werewolf.core.Game;

public class WerewolfMove implements GameMove {
    private final Game game;

    public WerewolfMove(final Game game) {

        this.game = game;
    }

    @Override
    public void execute() {
        game.getPlayers().stream().forEach(player -> player.user().tell("Not inplemented yet"));

    }
}
